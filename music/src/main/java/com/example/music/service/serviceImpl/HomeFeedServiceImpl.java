package com.example.music.service.serviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.music.model.VideoSummary;
import com.example.music.service.HomeFeedService;
import com.example.music.service.api.YoutubeApiClient;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeFeedServiceImpl implements HomeFeedService {

	private final YoutubeApiClient youtube;

	@Override
	public List<VideoSummary> getPopularForHome(String regionCode, int limit) throws Exception {
		JsonNode items = youtube.fetchMostPopular(regionCode, limit, null);
		List<VideoSummary> list = new ArrayList<>();
		List<String> channelIds = new ArrayList<>();

		for (JsonNode item : items) {
			JsonNode sn = item.get("snippet");
			JsonNode cd = item.get("contentDetails");
			JsonNode st = item.get("statistics");

			VideoSummary v = new VideoSummary();
			v.setId(item.get("id").asText());
			v.setTitle(sn.get("title").asText());
			v.setDescription(sn.path("description").asText(""));
			v.setThumbnail(sn.path("thumbnails").path("medium").path("url").asText());
			v.setChannelId(sn.get("channelId").asText()); // ← 채널 ID 세팅
			v.setChannelTitle(sn.get("channelTitle").asText());

			String iso = cd.get("duration").asText();
			v.setFormattedDuration(formatIsoDuration(iso));

			long views = st.has("viewCount") ? st.get("viewCount").asLong() : 0L;
			v.setViewCount(views);
			v.setFormattedViewCount(formatViewCountKR(views));

			var publishedAt = sn.get("publishedAt").asText();
			v.setPublishedDate(Date.from(Instant.parse(publishedAt)));

			list.add(v);
			channelIds.add(v.getChannelId());
		}

		// 중복 제거 후 채널 썸네일 배치 조회
		Map<String, String> channelThumbMap = youtube
				.fetchChannelThumbnails(channelIds.stream().distinct().collect(Collectors.toList()));

		// DTO에 채널 썸네일 주입
		for (VideoSummary v : list) {
			v.setChannelThumbnail(channelThumbMap.getOrDefault(v.getChannelId(), null));
		}

		return list;
	}

	private String formatIsoDuration(String iso) {
		Duration d = Duration.parse(iso);
		long s = d.getSeconds();
		long h = s / 3600;
		s %= 3600;
		long m = s / 60;
		s %= 60;
		if (h > 0) {
			return String.format("%d:%02d:%02d", h, m, s);
		}
		return String.format("%d:%02d", m, s);
	}

	private String formatViewCountKR(long v) {
		if (v >= 100_000_000) {
			return (v / 100_000_000) + "억회";
		}
		if (v >= 10_000) {
			return (v / 10_000) + "만회";
		}
		return v + "회";
	}

}
