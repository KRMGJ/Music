package com.example.music.service.serviceImpl;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.music.dao.VideoDao;
import com.example.music.model.Comments;
import com.example.music.model.Video;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.service.VideoService;
import com.example.music.service.api.YoutubeApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	VideoDao videoDao;

	@Autowired
	YoutubeApiClient youtubeApiClient;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	@Override
	public void insertVideo(Video video) {
		Video existingVideo = videoDao.getVideoById(video.getVideoId());
		if (existingVideo != null) {
			videoDao.updateVideo(video.getVideoId());
			return;
		}
		videoDao.insertVideo(video);
	}

	@Override
	public void updateVideo(String videoId) {
		videoDao.updateVideo(videoId);
	}

	@Override
	public void deleteVideo(String id) {
		videoDao.deleteVideo(id);
	}

	@Override
	public List<Video> getAllVideos() {
		List<Video> videoList = videoDao.getAllVideos();
		return videoList;
	}

	@Override
	public List<Video> getVideosByChannel(String channelTitle) {
		List<Video> videoList = videoDao.getVideosByChannel(channelTitle);
		return videoList;
	}

	@Override
	@Cacheable(cacheNames = "videoDetail", key = "#id")
	public VideoDetail getDetail(String id) {
		try {
			JsonNode snippet = youtubeApiClient.fetchSnippetById(id);
			if (snippet == null || snippet.isMissingNode()) {
				throw new RuntimeException("video snippet not found");
			}

			String title = optText(snippet, "title");
			String description = optText(snippet, "description");
			String channelId = optText(snippet, "channelId");
			String channelTitle = optText(snippet, "channelTitle");
			String thumb = bestThumbUrl(snippet.path("thumbnails"));
			String publishedAtIso = optText(snippet, "publishedAt");
			String publishedDate = formatDate(publishedAtIso);

			Map<String, JsonNode> details = youtubeApiClient.fetchVideoDetails(Collections.singletonList(id));
			JsonNode detailNode = details.get(id);
			String isoDuration = detailNode != null ? optText(detailNode.path("contentDetails"), "duration") : null;
			String formattedDuration = formatDuration(isoDuration);
			long viewCount = detailNode != null ? optLong(detailNode.path("statistics"), "viewCount") : 0L;
			String formattedViewCount = formatViewCountKR(viewCount);

			String channelThumb = null;
			String formattedSubs = "비공개";
			if (channelId != null && !channelId.isEmpty()) {
				JsonNode ch = youtubeApiClient.fetchChannelInfo(channelId);
				if (ch != null && !ch.isMissingNode()) {
					channelThumb = bestThumbUrl(ch.path("snippet").path("thumbnails"));
					long subs = optLong(ch.path("statistics"), "subscriberCount");
					if (subs > 0) {
						formattedSubs = formatSubscribersKR(subs);
					}
				}
			}

			return VideoDetail.builder().id(id).title(title).description(description).channelTitle(channelTitle)
					.channelThumbnail(channelThumb).formattedSubscriberCount(formattedSubs)
					.formattedViewCount(formattedViewCount).publishedDate(publishedDate).thumbnail(thumb)
					.formattedDuration(formattedDuration).build();

		} catch (Exception e) {
			log.error("getDetail failed for id=" + id, e);
			return VideoDetail.builder().id(id).title("정보를 불러올 수 없습니다").description("").channelTitle("")
					.channelThumbnail(null).formattedSubscriberCount("비공개").formattedViewCount("0회").publishedDate("")
					.thumbnail(null).formattedDuration("").build();
		}
	}

	@Override
	@Cacheable(cacheNames = "videoRelated", key = "#id + ':' + #limit")
	public List<VideoListItem> getRelated(String id, int limit) {
		RelatedResponse resp = getRelatedPage(id, null, limit);
		return resp.getItems();
	}

	@Override
	@Cacheable(cacheNames = "videoRelatedPage", key = "#id + ':' + #pageToken + ':' + #size")
	public RelatedResponse getRelatedPage(String id, String pageToken, int size) {
		log.info("Fetching related videos for id={}, pageToken={}, size={}", id, pageToken, size);
		try {
			VideoDetail base = getDetail(id);
			if (base == null || base.getTitle() == null) {
				return new RelatedResponse(Collections.emptyList(), null);
			}

			JsonNode response = youtubeApiClient.fetchRelatedByTitle(base.getTitle(), size, pageToken);
			if (response == null || response.isMissingNode()) {
				return new RelatedResponse(Collections.emptyList(), null);
			}

			String nextToken = response.has("nextPageToken") ? response.get("nextPageToken").asText() : null;
			JsonNode items = response.path("items");
			if (items == null || !items.isArray() || items.size() == 0) {
				return new RelatedResponse(Collections.emptyList(), nextToken);
			}

			List<VideoListItem> result = parseItems(items, id);
			return new RelatedResponse(result, nextToken);

		} catch (Exception e) {
			log.error("getRelatedPage failed for id=" + id, e);
			return new RelatedResponse(Collections.emptyList(), null);
		}
	}

	@Override
	@Cacheable(cacheNames = "videoComments", key = "#videoId + ':' + #pageToken + ':' + #order + ':' + #pageSize")
	public Comments.Page getComments(String videoId, String pageToken, String order, Integer pageSize) {
		try {
			CommentThreadListResponse resp = youtubeApiClient.fetchCommentThreads(videoId, pageToken, order, pageSize);

			List<Comments.Comment> list = new ArrayList<>();
			if (resp.getItems() != null) {
				for (CommentThread thread : resp.getItems()) {
					// 최상위 댓글
					Comment top = thread.getSnippet() != null ? thread.getSnippet().getTopLevelComment() : null;
					CommentSnippet topSn = top != null ? top.getSnippet() : null;

					Comments.Comment.CommentBuilder cb = Comments.Comment.builder().id(top != null ? top.getId() : null)
							.authorDisplayName(topSn != null ? topSn.getAuthorDisplayName() : null)
							.authorProfileImageUrl(topSn != null ? topSn.getAuthorProfileImageUrl() : null)
							.textDisplay(topSn != null ? topSn.getTextDisplay() : null)
							.publishedAt(topSn != null && topSn.getPublishedAt() != null
									? topSn.getPublishedAt().toStringRfc3339()
									: null)
							.updatedAt(topSn != null && topSn.getUpdatedAt() != null
									? topSn.getUpdatedAt().toStringRfc3339()
									: null)
							.likeCount(topSn != null ? topSn.getLikeCount() : null)
							.totalReplyCount(thread.getSnippet() != null ? thread.getSnippet().getTotalReplyCount() : 0);

					// 대댓글 (일부만 포함될 수 있음)
					List<Comments.Reply> replies = new ArrayList<>();
					if (thread.getReplies() != null && thread.getReplies().getComments() != null) {
						for (Comment r : thread.getReplies().getComments()) {
							CommentSnippet rs = r.getSnippet();
							replies.add(Comments.Reply.builder().id(r.getId())
									.authorDisplayName(rs != null ? rs.getAuthorDisplayName() : null)
									.authorProfileImageUrl(rs != null ? rs.getAuthorProfileImageUrl() : null)
									.textDisplay(rs != null ? rs.getTextDisplay() : null)
									.publishedAt(topSn != null && topSn.getPublishedAt() != null
											? topSn.getPublishedAt().toStringRfc3339()
											: null)
									.updatedAt(topSn != null && topSn.getUpdatedAt() != null
											? topSn.getUpdatedAt().toStringRfc3339()
											: null)
									.likeCount(rs != null ? rs.getLikeCount() : null).build());
						}
					}
					cb.replies(replies);
					list.add(cb.build());
				}
			}

			Integer resultsPerPage = resp.getPageInfo() != null ? resp.getPageInfo().getResultsPerPage() : null;
			Integer totalResults = resp.getPageInfo() != null ? resp.getPageInfo().getTotalResults() : null;

			return Comments.Page.builder().items(list).nextPageToken(resp.getNextPageToken())
					.resultsPerPage(resultsPerPage).totalResults(totalResults).build();

		} catch (Exception e) {
			log.error("getComments failed for videoId=" + videoId, e);
			throw new RuntimeException("YouTube 댓글 조회 실패: " + e.getMessage(), e);
		}
	}

	// -------------------- helpers --------------------

	private List<VideoListItem> parseItems(JsonNode items, String excludeId) throws Exception {
		List<String> ids = new ArrayList<>();
		List<RelSeed> seeds = new ArrayList<>();

		for (JsonNode it : items) {
			String vid = optText(it.path("id"), "videoId");
			if (vid == null || vid.isEmpty() || vid.equals(excludeId))
				continue;

			JsonNode sn = it.path("snippet");
			String vTitle = optText(sn, "title");
			String vThumb = bestThumbUrl(sn.path("thumbnails"));
			String vChannelTitle = optText(sn, "channelTitle");
			String vPublished = formatDate(optText(sn, "publishedAt"));

			ids.add(vid);
			seeds.add(new RelSeed(vid, vTitle, vThumb, vChannelTitle, vPublished));
		}

		if (ids.isEmpty())
			return Collections.emptyList();

		Map<String, JsonNode> map = youtubeApiClient.fetchVideoDetails(ids);

		List<VideoListItem> result = new ArrayList<>();
		for (RelSeed s : seeds) {
			JsonNode d = map.get(s.id);
			String dur = d != null ? formatDuration(optText(d.path("contentDetails"), "duration")) : "";
			long vc = d != null ? optLong(d.path("statistics"), "viewCount") : 0L;
			result.add(VideoListItem.builder().id(s.id).title(s.title).thumbnail(s.thumb).channelTitle(s.channelTitle)
					.publishedDate(s.published).formattedDuration(dur).formattedViewCount(formatViewCountKR(vc))
					.build());
		}
		return result;
	}

	private static class RelSeed {
		String id, title, thumb, channelTitle, published;

		RelSeed(String id, String title, String thumb, String channelTitle, String published) {
			this.id = id;
			this.title = title;
			this.thumb = thumb;
			this.channelTitle = channelTitle;
			this.published = published;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RelatedResponse {
		private List<VideoListItem> items;
		private String nextPageToken;
	}

	private static String optText(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
	}

	private static long optLong(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asLong(0L) : 0L;
	}

	private static String bestThumbUrl(JsonNode thumbs) {
		if (thumbs == null || thumbs.isMissingNode())
			return null;
		if (thumbs.has("high"))
			return optText(thumbs.get("high"), "url");
		if (thumbs.has("medium"))
			return optText(thumbs.get("medium"), "url");
		if (thumbs.has("default"))
			return optText(thumbs.get("default"), "url");
		return null;
	}

	private static String formatDate(String iso) {
		if (iso == null || iso.isEmpty())
			return "";
		try {
			OffsetDateTime odt = OffsetDateTime.parse(iso);
			return DATE_FMT.format(odt);
		} catch (Exception e) {
			return "";
		}
	}

	private static String formatDuration(String iso) {
		if (iso == null || iso.isEmpty())
			return "";
		int h = 0, m = 0, s = 0;
		String t = iso.replace("PT", "");
		String num = "";
		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			if (Character.isDigit(c)) {
				num += c;
			} else {
				if (c == 'H') {
					h = parseInt(num);
					num = "";
				} else if (c == 'M') {
					m = parseInt(num);
					num = "";
				} else if (c == 'S') {
					s = parseInt(num);
					num = "";
				}
			}
		}
		if (h > 0)
			return String.format("%d:%02d:%02d", h, m, s);
		return String.format("%d:%02d", m, s);
	}

	private static int parseInt(String n) {
		try {
			return Integer.parseInt(n);
		} catch (Exception e) {
			return 0;
		}
	}

	private static String formatViewCountKR(long n) {
		if (n >= 100_000_000L)
			return (n / 100_000_000L) + "억회";
		if (n >= 10_000L)
			return (n / 10_000L) + "만회";
		return String.format("%,d회", n);
	}

	private static String formatSubscribersKR(long n) {
		if (n >= 100_000_000L)
			return (n / 100_000_000L) + "억명";
		if (n >= 10_000L)
			return (n / 10_000L) + "만명";
		return String.format("%,d명", n);
	}
}