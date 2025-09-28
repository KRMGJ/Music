package com.example.music.service.serviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.music.model.ChannelSummary;
import com.example.music.model.VideoSummary;
import com.example.music.service.HomeFeedService;
import com.example.music.service.api.YoutubeApiClient;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeFeedServiceImpl implements HomeFeedService {

	private final YoutubeApiClient youtube;

	@Cacheable(cacheNames = "home:popularVideos", key = "'region=' + #regionCode + ',limit=' + #limit")
	public List<VideoSummary> getPopularForHome(String regionCode, int limit) throws Exception {
		int fetch = Math.max(limit, 12); // 안전한 최소치
		JsonNode items = youtube.fetchMostPopular(regionCode, fetch, null);

		List<VideoSummary> list = new ArrayList<>();
		List<String> channelIds = new ArrayList<>();

		for (JsonNode item : items) {
			JsonNode sn = item.get("snippet");
			JsonNode cd = item.get("contentDetails");
			JsonNode st = item.get("statistics");

			VideoSummary v = new VideoSummary();
			v.setId(item.get("id").asText());
			v.setTitle(sn.path("title").asText());
			v.setDescription(sn.path("description").asText(""));
			v.setThumbnail(sn.path("thumbnails").path("medium").path("url").asText());
			v.setChannelId(sn.path("channelId").asText());
			v.setChannelTitle(sn.path("channelTitle").asText());

			String iso = cd.path("duration").asText("PT0S");
			v.setFormattedDuration(formatIsoDuration(iso));

			long views = st.has("viewCount") ? st.get("viewCount").asLong() : 0L;
			v.setViewCount(views);
			v.setFormattedViewCount(formatViewCountKR(views));

			String publishedAt = sn.path("publishedAt").asText(null);
			if (publishedAt != null && !publishedAt.isEmpty()) {
				v.setPublishedDate(Date.from(Instant.parse(publishedAt)));
			}

			list.add(v);
			if (v.getChannelId() != null)
				channelIds.add(v.getChannelId());
		}

		// 채널 썸네일 주입
		Map<String, String> chThumb = youtube
				.fetchChannelThumbnails(channelIds.stream().distinct().collect(Collectors.toList()));
		for (VideoSummary v : list) {
			v.setChannelThumbnail(chThumb.getOrDefault(v.getChannelId(), null));
		}

		// limit만큼 자르기
		if (list.size() > limit)
			return list.subList(0, limit);
		return list;
	}

	@Override
	public List<VideoSummary> getLatestForHome(String regionCode, int limit) throws Exception {
		// 인기풀을 넉넉히 받아 정렬 (최소 30~50 추천)
		int pool = Math.max(limit * 3, 50);

		var items = youtube.fetchMostPopular(regionCode, pool, null); // part=snippet,contentDetails,statistics

		List<VideoSummary> list = new ArrayList<>();
		List<String> channelIds = new ArrayList<>();

		for (JsonNode item : items) {
			JsonNode sn = item.get("snippet");
			JsonNode cd = item.get("contentDetails");
			JsonNode st = item.get("statistics");

			VideoSummary v = new VideoSummary();
			v.setId(item.get("id").asText());
			v.setTitle(sn.path("title").asText());
			v.setDescription(sn.path("description").asText(""));
			v.setThumbnail(sn.path("thumbnails").path("medium").path("url").asText());

			v.setChannelId(sn.path("channelId").asText());
			v.setChannelTitle(sn.path("channelTitle").asText());

			String iso = cd.path("duration").asText("PT0S");
			v.setFormattedDuration(formatIsoDuration(iso));

			long views = st.has("viewCount") ? st.get("viewCount").asLong() : 0L;
			v.setViewCount(views);
			v.setFormattedViewCount(formatViewCountKR(views));

			String publishedAt = sn.path("publishedAt").asText();
			if (publishedAt != null && !publishedAt.isEmpty()) {
				v.setPublishedDate(Date.from(Instant.parse(publishedAt)));
			}

			list.add(v);
			channelIds.add(v.getChannelId());
		}

		// 채널 썸네일 배치 조회
		Map<String, String> thumbMap = youtube
				.fetchChannelThumbnails(channelIds.stream().distinct().collect(java.util.stream.Collectors.toList()));
		for (VideoSummary v : list) {
			v.setChannelThumbnail(thumbMap.getOrDefault(v.getChannelId(), null));
		}

		// 최신순으로 정렬 후 limit
		list.sort((a, b) -> {
			Date da = a.getPublishedDate(), db = b.getPublishedDate();
			long la = da == null ? 0 : da.getTime();
			long lb = db == null ? 0 : db.getTime();
			return Long.compare(lb, la); // desc
		});
		if (list.size() > limit) {
			return list.subList(0, limit);
		}
		return list;
	}

	@Override
	public List<ChannelSummary> getPopularChannels(String regionCode, int limit) throws Exception {
		int pool = Math.max(limit * 3, 50);
		var trending = youtube.fetchMostPopular(regionCode, pool, null);

		java.util.Set<String> channelIds = new java.util.LinkedHashSet<>();
		for (JsonNode item : trending) {
			String chId = item.path("snippet").path("channelId").asText(null);
			if (chId != null) {
				channelIds.add(chId);
			}
		}

		var details = youtube.fetchChannelsDetails(new java.util.ArrayList<>(channelIds));

		List<ChannelSummary> list = new java.util.ArrayList<>();
		for (String chId : channelIds) {
			JsonNode d = details.get(chId);
			if (d == null) {
				continue;
			}
			JsonNode sn = d.path("snippet");
			JsonNode st = d.path("statistics");

			ChannelSummary cs = new ChannelSummary();
			cs.setChannelId(chId);
			cs.setChannelTitle(sn.path("title").asText(""));
			JsonNode thumbs = sn.path("thumbnails");
			String thumb = thumbs.has("high") ? thumbs.get("high").get("url").asText()
					: thumbs.has("medium") ? thumbs.get("medium").get("url").asText()
							: thumbs.has("default") ? thumbs.get("default").get("url").asText() : null;
			cs.setChannelThumbnail(thumb);
			// 비공개면 필드가 없을 수 있음
			cs.setSubscriberCount(st.has("subscriberCount") ? st.get("subscriberCount").asLong() : 0L);
//			cs.setVideoCount(st.has("videoCount") ? st.get("videoCount").asLong() : null);

			list.add(cs);
		}

		// 구독자수 기준 내림차순 정렬 (동률이면 videoCount)
		list.sort((a, b) -> {
			long sa = a.getSubscriberCount() == null ? 0 : a.getSubscriberCount();
			long sb = b.getSubscriberCount() == null ? 0 : b.getSubscriberCount();
			if (sa != sb) {
				return Long.compare(sb, sa);
			}
			long va = a.getVideoCount() == null ? 0 : a.getVideoCount();
			long vb = b.getVideoCount() == null ? 0 : b.getVideoCount();
			return Long.compare(vb, va);
		});

		return list.size() > limit ? list.subList(0, limit) : list;
	}

	private String formatIsoDuration(String iso) {
		Duration d = Duration.parse(iso);
		long s = d.getSeconds();
		long h = s / 3600;
		s %= 3600;
		long m = s / 60;
		s %= 60;
		if (h > 0)
			return String.format("%d:%02d:%02d", h, m, s);
		return String.format("%d:%02d", m, s);
	}

	private String formatViewCountKR(long v) {
		if (v >= 100_000_000)
			return (v / 100_000_000) + "억회";
		if (v >= 10_000)
			return (v / 10_000) + "만회";
		return v + "회";
	}

}
