package com.example.music.service.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class YoutubeApiClient {

//    private static final String API_KEY = "AIzaSyBBNgTGf8f93anD6oYRWSFBJe388DBXBQg";
	private static final String API_KEY = "AIzaSyBEZw8L1Tfnmc0O_qlqtOltVDTV8JRZPRc";
	private static final String API_URL = "https://www.googleapis.com/youtube/v3";
	private static final String CHANNEL_URL = API_URL + "/channels";
	private static final String SEARCH_URL = API_URL + "/search";
	private static final String DETAILS_URL = API_URL + "/videos";
	private static final ObjectMapper mapper = new ObjectMapper();

	public JsonNode fetchChannelInfo(String channelId) throws Exception {
		String url = CHANNEL_URL + "?part=snippet,statistics" + "&id=" + channelId + "&key=" + API_KEY;

		JsonNode response = mapper.readTree(sendGetRequest(url));
		return response.get("items").get(0); // 단일 채널 정보
	}

	public JsonNode searchVideos(String encodedQuery, String duration, String upload, String regionCode)
			throws Exception {
		StringBuilder url = new StringBuilder(
				SEARCH_URL + "?part=snippet&type=video&maxResults=20&q=" + encodedQuery + "&key=" + API_KEY);

		// 영상 길이 필터링 (API 지원: short, medium, long)
		if (duration != null && (duration.equals("short") || duration.equals("medium") || duration.equals("long"))) {
			url.append("&videoDuration=").append(duration);
		}

		// 지역 코드 필터링
		if (regionCode != null && !regionCode.isEmpty()) {
			url.append("&regionCode=").append(regionCode);
		}

		// 업로드 날짜 필터링 (publishedAfter 사용)
		if (upload != null) {
			ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
			ZonedDateTime cutoff = null;
			switch (upload) {
			case "1h":
				cutoff = now.minusHours(1);
				break;
			case "today":
				cutoff = now.toLocalDate().atStartOfDay(ZoneOffset.UTC);
				break;
			case "week":
				cutoff = now.minusDays(7);
				break;
			case "month":
				cutoff = now.minusDays(30);
				break;
			case "year":
				cutoff = now.minusDays(365);
				break;
			}
			if (cutoff != null) {
				String isoTime = cutoff.format(DateTimeFormatter.ISO_INSTANT);
				url.append("&publishedAfter=").append(isoTime);
			}
		}

		return mapper.readTree(sendGetRequest(url.toString())).get("items");
	}

	public Map<String, JsonNode> fetchVideoDetails(List<String> videoIds) throws Exception {
		if (videoIds.isEmpty()) {
			return Collections.emptyMap();
		}

		String idsParam = String.join(",", videoIds);
		String url = DETAILS_URL + "?part=contentDetails,statistics" // ← 여기 statistics 추가
				+ "&id=" + idsParam + "&key=" + API_KEY;

		JsonNode items = mapper.readTree(sendGetRequest(url)).get("items");

		Map<String, JsonNode> result = new HashMap<>();
		for (JsonNode item : items) {
			result.put(item.get("id").asText(), item);
		}
		return result;
	}

	public JsonNode fetchSnippetById(String videoId) throws Exception {
		String url = DETAILS_URL + "?part=snippet" + "&id=" + videoId + "&key=" + API_KEY;

		JsonNode response = mapper.readTree(sendGetRequest(url));
		return response.get("items").get(0).get("snippet");
	}

	// 인기(트렌딩): videos.list + chart=mostPopular
	public JsonNode fetchMostPopular(String regionCode, int maxResults, String pageToken) throws Exception {
		StringBuilder url = new StringBuilder(DETAILS_URL).append("?part=snippet,contentDetails,statistics")
				.append("&chart=mostPopular").append("&maxResults=").append(maxResults > 0 ? maxResults : 20)
				.append("&key=").append(API_KEY);

		if (regionCode != null && !regionCode.isEmpty()) {
			url.append("&regionCode=").append(regionCode);
		}
		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}

		return mapper.readTree(sendGetRequest(url.toString())).get("items");
	}

	// 최신: search.list + order=date (필요 시 publishedAfter 병행)
	public JsonNode fetchLatest(int maxResults, String regionCode, String pageToken, String publishedAfterIso)
			throws Exception {
		StringBuilder url = new StringBuilder(SEARCH_URL).append("?part=snippet&type=video").append("&order=date")
				.append("&maxResults=").append(maxResults > 0 ? maxResults : 20).append("&key=").append(API_KEY);

		if (regionCode != null && !regionCode.isEmpty()) {
			url.append("&regionCode=").append(regionCode);
		}
		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}
		if (publishedAfterIso != null && !publishedAfterIso.isEmpty()) {
			url.append("&publishedAfter=").append(publishedAfterIso);
		}

		return mapper.readTree(sendGetRequest(url.toString())).get("items");
	}

	public Map<String, String> fetchChannelThumbnails(List<String> channelIds) throws Exception {
		if (channelIds == null || channelIds.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, String> out = new HashMap<>();

		// channels.list는 한 요청에 최대 50개 id 권장
		final int chunkSize = 50;
		for (int i = 0; i < channelIds.size(); i += chunkSize) {
			int end = Math.min(i + chunkSize, channelIds.size());
			String idsParam = String.join(",", channelIds.subList(i, end));

			String url = CHANNEL_URL + "?part=snippet" + "&id=" + idsParam + "&fields=items(id,snippet/thumbnails)" // 응답
																													// 최소화(선택)
					+ "&key=" + API_KEY;

			JsonNode items = mapper.readTree(sendGetRequest(url)).get("items");
			if (items == null) {
				continue;
			}

			for (JsonNode it : items) {
				String id = it.get("id").asText();
				JsonNode thumbs = it.path("snippet").path("thumbnails");
				String thumbUrl = bestThumbUrl(thumbs); // high → medium → default 우선
				if (thumbUrl != null) {
					out.put(id, thumbUrl);
				}
			}
		}
		return out;
	}

	public Map<String, JsonNode> fetchChannelsDetails(java.util.List<String> channelIds) throws Exception {
		if (channelIds == null || channelIds.isEmpty()) {
			return java.util.Collections.emptyMap();
		}
		Map<String, JsonNode> out = new java.util.HashMap<>();
		final int chunk = 50;
		for (int i = 0; i < channelIds.size(); i += chunk) {
			int end = Math.min(i + chunk, channelIds.size());
			String ids = String.join(",", channelIds.subList(i, end));
			String url = CHANNEL_URL + "?part=snippet,statistics" + "&id=" + ids + "&key=" + API_KEY;
			JsonNode items = mapper.readTree(sendGetRequest(url)).get("items");
			if (items == null) {
				continue;
			}
			for (JsonNode it : items) {
				out.put(it.get("id").asText(), it);
			}
		}
		return out;
	}

	private String bestThumbUrl(JsonNode thumbs) {
		if (thumbs == null || thumbs.isMissingNode()) {
			return null;
		}
		if (thumbs.has("high")) {
			return thumbs.get("high").get("url").asText();
		}
		if (thumbs.has("medium")) {
			return thumbs.get("medium").get("url").asText();
		}
		if (thumbs.has("default")) {
			return thumbs.get("default").get("url").asText();
		}
		return null;
	}

	private String sendGetRequest(String urlStr) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
		conn.setRequestMethod("GET");

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();
		}
	}
}
