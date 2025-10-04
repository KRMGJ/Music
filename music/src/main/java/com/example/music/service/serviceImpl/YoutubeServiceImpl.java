package com.example.music.service.serviceImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.music.dao.ChannelInfoDao;
import com.example.music.dao.VideoDao;
import com.example.music.model.ChannelInfo;
import com.example.music.model.CommentPostRequest;
import com.example.music.model.Comments;
import com.example.music.model.PageResponse;
import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.model.YoutubePlaylist;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import com.example.music.service.api.YoutubeApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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
public class YoutubeServiceImpl implements YoutubeService {

	@Autowired
	YoutubeApiClient youtubeApiClient;

	@Autowired
	VideoDao videoDao;

	@Autowired
	VideoService videoService;

	@Autowired
	ChannelInfoDao channelInfoDao;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	@Override
	public SearchList searchVideos(String query, int page, String sort, String duration, String upload,
			String regionCode) {
		try {
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
			JsonNode items = youtubeApiClient.searchVideos(encodedQuery, duration, upload, regionCode);

			// 채널 정보 수집
			Set<String> channelIds = new HashSet<>();
			for (JsonNode item : items) {
				String channelId = item.get("snippet").get("channelId").asText();
				channelIds.add(channelId);
			}

			Map<String, ChannelInfo> channelInfoMap = new HashMap<>();
			for (String channelId : channelIds) {
				JsonNode channelData = youtubeApiClient.fetchChannelInfo(channelId);
				String title = channelData.get("snippet").get("title").asText();
				String thumbnailUrl = channelData.get("snippet").get("thumbnails").get("default").get("url").asText();
				long subscriberCount = channelData.get("statistics").get("subscriberCount").asLong();
				channelInfoMap.put(channelId, new ChannelInfo(channelId, title, thumbnailUrl, subscriberCount));
			}

			// videoId 리스트 수집
			List<String> videoIds = new ArrayList<>();
			for (JsonNode item : items) {
				if (item.get("id").has("videoId")) {
					videoIds.add(item.get("id").get("videoId").asText());
				}
			}

			Map<String, JsonNode> videoDetails = youtubeApiClient.fetchVideoDetails(videoIds);

			// Video 객체 리스트 구성
			List<Video> videos = new ArrayList<>();
			for (JsonNode item : items) {
				JsonNode idNode = item.get("id");
				if (idNode == null || !idNode.has("videoId")) {
					continue;
				}

				String videoId = idNode.get("videoId").asText();
				JsonNode snippet = item.get("snippet");
				JsonNode videoDetail = videoDetails.get(videoId);

				if (snippet == null || videoDetail == null || videoDetail.get("statistics") == null
						|| videoDetail.get("contentDetails") == null) {
					continue;
				}

				String title = snippet.get("title").asText();
				String description = snippet.get("description").asText();
				String thumbnail = snippet.get("thumbnails").get("medium").get("url").asText();
				Timestamp publishedDate = Timestamp
						.from(OffsetDateTime.parse(snippet.get("publishedAt").asText()).toInstant());
				long viewCount = videoDetail.get("statistics").get("viewCount").asLong();
				String formattedViewCount = formatViewCount(viewCount);

				String durationStr = videoDetail.get("contentDetails").get("duration").asText();
				int durationSec = (int) Duration.parse(durationStr).getSeconds();
				String formattedDuration = getFormattedDuration(durationSec);

				String channelId = snippet.get("channelId").asText();
				ChannelInfo channelInfo = channelInfoMap.get(channelId);

				videos.add(new Video(videoId, channelId, title, thumbnail, description, durationStr, durationSec,
						formattedDuration, publishedDate, viewCount, formattedViewCount, channelInfo,
						channelInfo.getChannelTitle(), channelInfo.getChannelThumbnail(),
						channelInfo.getSubscriberCount()));
			}

			// 정렬
			switch (sort) {
			case "oldest":
				videos.sort(Comparator.comparing(Video::getPublishedDate));
				break;
			case "latest":
				videos.sort(Comparator.comparing(Video::getPublishedDate).reversed());
				break;
			case "views":
				videos.sort(Comparator.comparing(Video::getViewCount).reversed());
				break;
			default:
			}

			// 페이징
			int pageSize = 10;
			int totalCount = videos.size();
			int totalPages = (int) Math.ceil((double) totalCount / pageSize);
			List<Video> pagedList = paginate(videos, page, pageSize);

			return new SearchList(pagedList, totalCount, totalPages, page, sort);

		} catch (Exception e) {
			e.printStackTrace();
			return new SearchList(Collections.emptyList(), 0, 0, page, sort);
		}
	}

	@Override
	public Video fetchAndSaveVideoById(String videoId) {
		try {
			// 1. 이미 DB에 있는지 확인
			Video existing = videoDao.getVideoById(videoId);
			if (existing != null) {
				return existing;
			}

			// 2. API 호출해서 데이터 가져오기
			List<String> ids = Collections.singletonList(videoId);
			Map<String, JsonNode> videoDetails = youtubeApiClient.fetchVideoDetails(ids);

			if (!videoDetails.containsKey(videoId)) {
				throw new RuntimeException("YouTube API에서 해당 영상을 찾을 수 없습니다.");
			}

			JsonNode detail = videoDetails.get(videoId);
			JsonNode snippet = youtubeApiClient.fetchSnippetById(videoId);

			// 3. 데이터 추출
			String title = snippet.get("title").asText();
			String description = snippet.get("description").asText();
			String thumbnail = snippet.get("thumbnails").get("medium").get("url").asText();
			Timestamp publishedDate = Timestamp
					.from(OffsetDateTime.parse(snippet.get("publishedAt").asText()).toInstant());

			long viewCount = detail.get("statistics").get("viewCount").asLong();
			String formattedViewCount = formatViewCount(viewCount);

			String durationStr = detail.get("contentDetails").get("duration").asText();
			int durationSec = (int) Duration.parse(durationStr).getSeconds();
			String formattedDuration = getFormattedDuration(durationSec);

			String channelId = snippet.get("channelId").asText();
			JsonNode channelData = youtubeApiClient.fetchChannelInfo(channelId);
			String channelTitle = channelData.get("snippet").get("title").asText();
			String channelThumb = channelData.get("snippet").get("thumbnails").get("medium").get("url").asText();
			long subscriberCount = channelData.get("statistics").get("subscriberCount").asLong();
			ChannelInfo channelInfo = new ChannelInfo(channelId, channelTitle, channelThumb, subscriberCount);

			Video video = new Video(videoId, channelId, title, thumbnail, description, durationStr, durationSec,
					formattedDuration, publishedDate, viewCount, formattedViewCount, channelInfo,
					channelInfo.getChannelTitle(), channelInfo.getChannelThumbnail(), channelInfo.getSubscriberCount());

			// 3.1. 채널 정보 DB에 저장
			ChannelInfo existingChannel = channelInfoDao.getChannelInfoById(channelId);
			if (existingChannel == null) {
				channelInfoDao.insertChannelInfo(channelInfo);
			} else {
				channelInfoDao.updateChannelInfo(channelInfo);
			}

			// 4. DB 저장
			videoService.insertVideo(video);
			return video;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("영상 정보를 불러오는 중 오류 발생");
		}
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

			return VideoDetail.builder().id(id).title(title).description(description).channelId(channelId)
					.channelTitle(channelTitle).channelThumbnail(channelThumb).formattedSubscriberCount(formattedSubs)
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
		try {
			VideoDetail base = getDetail(id);
			if (base == null || base.getTitle() == null) {
				return new RelatedResponse(Collections.emptyList(), null);
			}

			// 1) 제목 기반 검색
			JsonNode response = youtubeApiClient.fetchRelatedByTitle(base.getTitle(), size, pageToken);
			JsonNode items = response.path("items");
			String nextToken = response.has("nextPageToken") ? response.get("nextPageToken").asText() : null;

			// 2) 만약 결과가 비었으면 → 채널 최신 영상 fallback
			if (items == null || !items.isArray() || items.size() == 0) {
				response = youtubeApiClient.fetchChannelLatest(base.getChannelId(), size, pageToken);
				items = response.path("items");
				nextToken = response.has("nextPageToken") ? response.get("nextPageToken").asText() : null;
			}

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
							.likeCount(topSn != null ? topSn.getLikeCount() : null).totalReplyCount(
									thread.getSnippet() != null ? thread.getSnippet().getTotalReplyCount() : 0);

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
					.resultsPerPage(resultsPerPage).totalResults(totalResults).commentsDisabled(false).build();

		} catch (GoogleJsonResponseException gjre) {
			String reason = null;
			if (gjre.getDetails() != null && gjre.getDetails().getErrors() != null
					&& !gjre.getDetails().getErrors().isEmpty()) {
				reason = gjre.getDetails().getErrors().get(0).getReason();
			}

			String msg;
			boolean disabled = false;

			if ("commentsDisabled".equalsIgnoreCase(reason)) {
				msg = "이 영상은 댓글이 사용 중지되었습니다.";
				disabled = true;
			} else if ("videoNotFound".equalsIgnoreCase(reason)) {
				msg = "영상을 찾을 수 없습니다.";
			} else if ("forbidden".equalsIgnoreCase(reason)) {
				msg = "이 영상은 접근이 제한되어 댓글을 불러올 수 없습니다.";
			} else if ("quotaExceeded".equalsIgnoreCase(reason)) {
				msg = "YouTube API 사용 한도를 초과했습니다. 잠시 후 다시 시도해 주세요.";
			} else {
				msg = "댓글을 불러올 수 없습니다. (" + reason + ")";
			}

			return Comments.Page.builder().items(Collections.emptyList()).nextPageToken(null).resultsPerPage(0)
					.totalResults(0).commentsDisabled(disabled).errorMessage(msg).build();

		} catch (Exception e) {
			throw new RuntimeException("YouTube 댓글 조회 실패: " + e.getMessage(), e);
		}
	}

	@Override
	public Object postComment(CommentPostRequest req, String accessToken) throws Exception {
		if (youtubeApiClient == null) {
			throw new IllegalStateException("YoutubeApiClient 주입 실패");
		}
		if (req.getVideoId() == null || req.getVideoId().isBlank()) {
			throw new IllegalArgumentException("videoId가 없습니다.");
		}
		if (req.getText() == null || req.getText().trim().isEmpty()) {
			throw new IllegalArgumentException("댓글 내용이 없습니다.");
		}

		return youtubeApiClient.insertTopLevelComment(accessToken, null, req.getVideoId(), req.getText().trim());
	}

	@Override
	@Cacheable(cacheNames = "myPlaylists", key = "#accessToken + ':' + #size + ':' + #pageToken")
	public PageResponse<YoutubePlaylist> getMyPlaylists(String accessToken, int size, String pageToken) {
		JsonNode root = youtubeApiClient.listMyPlaylists(accessToken, Math.min(Math.max(size, 1), 50), pageToken);

		List<YoutubePlaylist> list = new ArrayList<>();
		JsonNode items = root.path("items");
		if (items.isArray()) {
			for (JsonNode item : items) {
				YoutubePlaylist dto = new YoutubePlaylist();
				dto.setId(item.path("id").asText());

				JsonNode sn = item.path("snippet");
				dto.setTitle(sn.path("title").asText());

				String best = pickBestThumb(sn.path("thumbnails"));
				dto.setThumbnailUrl(best);

				dto.setItemCount(item.path("contentDetails").path("itemCount").asInt(0));
				list.add(dto);
			}
		}

		PageResponse<YoutubePlaylist> page = new PageResponse<>();
		page.setItems(list);
		page.setNextPageToken(root.path("nextPageToken").asText(null));
		page.setPrevPageToken(root.path("prevPageToken").asText(null));
		return page;
	}

	// -------------------- helpers --------------------

	private List<VideoListItem> parseItems(JsonNode items, String excludeId) throws Exception {
		List<String> ids = new ArrayList<>();
		List<RelSeed> seeds = new ArrayList<>();

		for (JsonNode it : items) {
			String vid = optText(it.path("id"), "videoId");
			if (vid == null || vid.isEmpty() || vid.equals(excludeId)) {
				continue;
			}

			JsonNode sn = it.path("snippet");
			String vTitle = optText(sn, "title");
			String vThumb = bestThumbUrl(sn.path("thumbnails"));
			String vChannelTitle = optText(sn, "channelTitle");
			String vPublished = formatDate(optText(sn, "publishedAt"));

			ids.add(vid);
			seeds.add(new RelSeed(vid, vTitle, vThumb, vChannelTitle, vPublished));
		}

		if (ids.isEmpty()) {
			return Collections.emptyList();
		}

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
		if (thumbs == null || thumbs.isMissingNode()) {
			return null;
		}
		if (thumbs.has("high")) {
			return optText(thumbs.get("high"), "url");
		}
		if (thumbs.has("medium")) {
			return optText(thumbs.get("medium"), "url");
		}
		if (thumbs.has("default")) {
			return optText(thumbs.get("default"), "url");
		}
		return null;
	}

	private static String formatDate(String iso) {
		if (iso == null || iso.isEmpty()) {
			return "";
		}
		try {
			OffsetDateTime odt = OffsetDateTime.parse(iso);
			return DATE_FMT.format(odt);
		} catch (Exception e) {
			return "";
		}
	}

	private static String formatDuration(String iso) {
		if (iso == null || iso.isEmpty()) {
			return "";
		}
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
		if (h > 0) {
			return String.format("%d:%02d:%02d", h, m, s);
		}
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
		if (n >= 100_000_000L) {
			return (n / 100_000_000L) + "억회";
		}
		if (n >= 10_000L) {
			return (n / 10_000L) + "만회";
		}
		return String.format("%,d회", n);
	}

	private static String formatSubscribersKR(long n) {
		if (n >= 100_000_000L) {
			return (n / 100_000_000L) + "억명";
		}
		if (n >= 10_000L) {
			return (n / 10_000L) + "만명";
		}
		return String.format("%,d명", n);
	}

	@Override
	public List<Video> paginate(List<Video> list, int page, int size) {
		int from = (page - 1) * size;
		int to = Math.min(from + size, list.size());
		if (from >= list.size()) {
			return Collections.emptyList();
		}
		return list.subList(from, to);
	}

	private String getFormattedDuration(int durationSec) {
		int hours = durationSec / 3600;
		int minutes = (durationSec % 3600) / 60;
		int remainingSeconds = durationSec % 60;

		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, remainingSeconds); // 예: 1:02:03
		} else {
			return String.format("%d:%02d", minutes, remainingSeconds); // 예: 2:45
		}
	}

	private String formatViewCount(long viewCount) {
		if (viewCount >= 100_000_000) {
			return (viewCount / 100_000_000) + "억회";
		} else if (viewCount >= 10_000) {
			return (viewCount / 10_000) + "만회";
		} else {
			return String.format("%,d회", viewCount); // 천 단위 콤마
		}
	}

	private static String pickBestThumb(JsonNode tn) {
		String[] order = { "maxres", "standard", "high", "medium", "default" };
		for (String key : order) {
			String url = tn.path(key).path("url").asText(null);
			if (url != null && !url.isEmpty()) {
				return url;
			}
		}
		return null;
	}
}
