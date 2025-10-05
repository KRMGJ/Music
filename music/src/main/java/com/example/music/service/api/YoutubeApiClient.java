package com.example.music.service.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadSnippet;

@Component
public class YoutubeApiClient {

	@Autowired
	YouTube Youtube;

	@Autowired
	RestTemplate restTemplate;

//	private static final String API_KEY = "AIzaSyBEZw8L1Tfnmc0O_qlqtOltVDTV8JRZPRc";
	private static final String API_KEY = "AIzaSyB-w0ykEEHVWU1eKqiJ6a8OQHs_CMX6c_g";
	private static final String API_URL = "https://www.googleapis.com/youtube/v3";
	private static final String KOREAN = "&relevanceLanguage=ko&regionCode=KR";
	private static final String CHANNEL_URL = API_URL + "/channels";
	private static final String SEARCH_URL = API_URL + "/search";
	private static final String DETAILS_URL = API_URL + "/videos";
	private static final ObjectMapper mapper = new ObjectMapper();

	private static final NetHttpTransport HTTP = new NetHttpTransport();
	private static final JacksonFactory JSON = JacksonFactory.getDefaultInstance();

	// 액세스 토큰 기반 클라이언트 (쓰기용)
	private YouTube clientWithToken(String accessToken) {
		return new YouTube.Builder(HTTP, JSON, req -> req.getHeaders().setAuthorization("Bearer " + accessToken))
				.setApplicationName("music").build();
	}

	/**
	 * 채널 정보 가져오기
	 * 
	 * @param channelId 채널 ID
	 * @return 채널 정보 JsonNode
	 * @throws Exception
	 */
	public JsonNode fetchChannelInfo(String channelId) throws Exception {
		String url = CHANNEL_URL + "?part=snippet,statistics" + "&id=" + channelId + "&key=" + API_KEY;

		JsonNode response = mapper.readTree(sendGetRequest(url));
		return response.get("items").get(0); // 단일 채널 정보
	}

	/**
	 * 동영상 검색
	 * 
	 * @param encodedQuery 검색어 (URL 인코딩된 상태)
	 * @param duration     영상 길이 필터 (short, medium, long) 또는 null
	 * @param upload       업로드 날짜 필터 (1h, today, week, month, year) 또는 null
	 * @param regionCode   지역 코드 필터 (예: US, KR) 또는 null
	 * @return 검색 결과 JsonNode 배열
	 * @throws Exception
	 */
	public JsonNode searchVideos(String encodedQuery, String duration, String upload, String regionCode)
			throws Exception {
		StringBuilder url = new StringBuilder(
				SEARCH_URL + "?part=snippet&type=video&maxResults=20&q=" + encodedQuery + "&key=" + API_KEY + KOREAN);

		if (duration != null && (duration.equals("short") || duration.equals("medium") || duration.equals("long"))) {
			url.append("&videoDuration=").append(duration);
		}

		if (regionCode != null && !regionCode.isEmpty()) {
			url.append("&regionCode=").append(regionCode);
		}

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

	/**
	 * 여러 비디오의 상세 정보 가져오기
	 * 
	 * @param videoIds 비디오 ID 리스트
	 * @return 비디오ID -> 상세정보(JsonNode) 맵
	 * @throws Exception
	 */
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

	/**
	 * 단일 비디오의 스니펫 정보 가져오기
	 * 
	 * @param videoId 비디오 ID
	 * @return 스니펫 정보 JsonNode
	 * @throws Exception
	 */
	public JsonNode fetchSnippetById(String videoId) throws Exception {
		String url = DETAILS_URL + "?part=snippet" + "&id=" + videoId + "&key=" + API_KEY;

		JsonNode response = mapper.readTree(sendGetRequest(url));
		return response.get("items").get(0).get("snippet");
	}

	/**
	 * 인기 동영상 가져오기
	 * 
	 * @param regionCode 지역 코드 (예: US, KR) 또는 null
	 * @param maxResults 최대 결과 수 (기본 20)
	 * @param pageToken  페이지 토큰 (다음 페이지 요청 시 사용) 또는 null
	 * @return 인기 동영상 JsonNode 배열
	 * @throws Exception
	 */
	public JsonNode fetchMostPopular(String regionCode, int maxResults, String pageToken) throws Exception {
		StringBuilder url = new StringBuilder(DETAILS_URL).append("?part=snippet,contentDetails,statistics")
				.append("&chart=mostPopular").append("&maxResults=").append(maxResults > 0 ? maxResults : 20)
				.append("&key=").append(API_KEY).append(KOREAN);
		if (regionCode != null && !regionCode.isEmpty()) {
			url.append("&regionCode=").append(regionCode);
		}
		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}
		return mapper.readTree(sendGetRequest(url.toString())).get("items");
	}

	/**
	 * 최신 동영상 가져오기
	 * 
	 * @param maxResults        최대 결과 수 (기본 20)
	 * @param regionCode        지역 코드 (예: US, KR) 또는 null
	 * @param pageToken         페이지 토큰 (다음 페이지 요청 시 사용) 또는 null
	 * @param publishedAfterIso ISO 8601 형식의 날짜 문자열 (예: 2023-01-01T00:00:00Z) 또는
	 *                          null
	 * @return 최신 동영상 JsonNode 배열
	 * @throws Exception
	 */
	public JsonNode fetchLatest(int maxResults, String regionCode, String pageToken, String publishedAfterIso)
			throws Exception {
		StringBuilder url = new StringBuilder(SEARCH_URL).append("?part=snippet&type=video").append("&order=date")
				.append("&maxResults=").append(maxResults > 0 ? maxResults : 20).append("&key=").append(API_KEY)
				.append(KOREAN);

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

	/**
	 * 채널 썸네일 URL 맵 가져오기
	 * 
	 * @param channelIds 채널 ID 리스트
	 * @return 채널ID -> 썸네일URL 맵
	 * @throws Exception
	 */
	public Map<String, String> fetchChannelThumbnails(List<String> channelIds) throws Exception {
		if (channelIds == null || channelIds.isEmpty()) {
			return java.util.Collections.emptyMap();
		}
		Map<String, String> out = new HashMap<>();
		final int chunkSize = 50;
		for (int i = 0; i < channelIds.size(); i += chunkSize) {
			int end = Math.min(i + chunkSize, channelIds.size());
			String idsParam = String.join(",", channelIds.subList(i, end));
			String url = CHANNEL_URL + "?part=snippet&id=" + idsParam + "&key=" + API_KEY;
			JsonNode items = mapper.readTree(sendGetRequest(url)).get("items");
			if (items == null) {
				continue;
			}
			for (JsonNode it : items) {
				String id = it.get("id").asText();
				JsonNode thumbs = it.path("snippet").path("thumbnails");
				String thumb = thumbs.has("high") ? thumbs.get("high").get("url").asText()
						: thumbs.has("medium") ? thumbs.get("medium").get("url").asText()
								: thumbs.has("default") ? thumbs.get("default").get("url").asText() : null;
				if (thumb != null) {
					out.put(id, thumb);
				}
			}
		}
		return out;
	}

	/**
	 * 채널 상세 정보 가져오기
	 * 
	 * @param channelIds 채널 ID 리스트
	 * @return 채널ID -> 상세정보(JsonNode) 맵
	 * @throws Exception
	 */
	public Map<String, JsonNode> fetchChannelsDetails(List<String> channelIds) throws Exception {
		if (channelIds == null || channelIds.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, JsonNode> out = new HashMap<>();
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

	/**
	 * 특정 채널의 최신 동영상 검색
	 * 
	 * @param channelId  대상 채널 ID
	 * @param maxResults 최대 결과 수 (기본 12)
	 * @param pageToken  다음 페이지 토큰 (없으면 null)
	 * @return 최신 동영상 JsonNode 배열
	 * @throws Exception
	 */
	public JsonNode fetchChannelLatest(String channelId, int maxResults, String pageToken) throws Exception {
		StringBuilder url = new StringBuilder(SEARCH_URL).append("?part=snippet&type=video").append("&order=date")
				.append("&channelId=").append(channelId).append("&maxResults=").append(maxResults > 0 ? maxResults : 12)
				.append("&key=").append(API_KEY);

		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}

		return mapper.readTree(sendGetRequest(url.toString()));
	}

	/**
	 * 제목 기반으로 관련 동영상 검색
	 * 
	 * @param title      기준 제목
	 * @param maxResults 최대 결과 수 (기본 12)
	 * @return 관련 동영상 JsonNode 배열
	 * @throws Exception
	 */
	public JsonNode fetchRelatedByTitle(String title, int maxResults, String pageToken) throws Exception {
		if (title == null || title.isEmpty()) {
			return mapper.createObjectNode().putArray("items"); // 빈 items 반환
		}

		String query = title.length() > 60 ? title.substring(0, 60) : title;

		StringBuilder url = new StringBuilder(SEARCH_URL).append("?part=snippet").append("&type=video")
				.append("&maxResults=").append(maxResults > 0 ? maxResults : 12).append("&q=")
				.append(URLEncoder.encode(query, StandardCharsets.UTF_8)).append("&key=").append(API_KEY)
				.append(KOREAN);

		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}

		return mapper.readTree(sendGetRequest(url.toString()));
	}

	/**
	 * 댓글 스레드 조회
	 * 
	 * @param videoId   대상 영상 ID
	 * @param pageToken 다음 페이지 토큰 (없으면 null)
	 * @param order     "time" | "relevance" (null이면 time)
	 * @param pageSize  1~100 (null 또는 범위 밖이면 20)
	 */
	public CommentThreadListResponse fetchCommentThreads(String videoId, String pageToken, String order,
			Integer pageSize) throws IOException {
		if (order == null || order.isBlank()) {
			order = "time";
		}
		long max = 20;
		if (pageSize != null && pageSize >= 1 && pageSize <= 100) {
			max = pageSize;
		}

		YouTube.CommentThreads.List req = Youtube.commentThreads().list("snippet,replies").setVideoId(videoId)
				.setOrder(order).setMaxResults(max).setKey(API_KEY);

		if (pageToken != null && !pageToken.isBlank()) {
			req.setPageToken(pageToken);
		}
		return req.execute();
	}

	/**
	 * * 최상위 댓글 작성
	 * 
	 * @param accessToken OAuth2 액세스 토큰
	 * @param channelId   업로더 채널 ID
	 * @param videoId     대상 영상 ID
	 * @param text        댓글 내용
	 * @return 작성된 댓글 스레드 객체
	 * @throws Exception
	 */
	public CommentThread insertTopLevelComment(String accessToken, String channelId, String videoId, String text)
			throws IOException {
		YouTube yt = clientWithToken(accessToken);

		CommentSnippet topSnippet = new CommentSnippet();
		topSnippet.setTextOriginal(text);

		Comment top = new Comment();
		top.setSnippet(topSnippet);

		CommentThreadSnippet threadSnippet = new CommentThreadSnippet();
		// 영상 댓글이면 videoId만 필수
		threadSnippet.setVideoId(videoId);
		// 채널 댓글을 의도한 경우에만 세팅
		if (channelId != null && !channelId.isBlank()) {
			threadSnippet.setChannelId(channelId);
		}
		threadSnippet.setTopLevelComment(top);

		CommentThread body = new CommentThread();
		body.setSnippet(threadSnippet);

		return yt.commentThreads().insert("snippet", body).execute();
	}

	/**
	 * 내(로그인한 사용자)의 플레이리스트 목록 조회
	 * 
	 * @param accessToken OAuth2 Access Token (Google)
	 * @param maxResults  1~50
	 * @param pageToken   null 가능 (다음 페이지용)
	 */
	public JsonNode listMyPlaylists(String accessToken, int maxResults, String pageToken) {
		StringBuilder url = new StringBuilder("https://www.googleapis.com/youtube/v3/playlists")
				.append("?part=snippet,contentDetails").append("&mine=true").append("&maxResults=").append(maxResults);

		if (pageToken != null && !pageToken.isEmpty()) {
			url.append("&pageToken=").append(pageToken);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> resp = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, String.class);

		try {
			return mapper.readTree(resp.getBody());
		} catch (Exception e) {
			throw new RuntimeException("유튜브 플레이리스트 조회 실패", e);
		}
	}

	/**
	 * 플레이리스트 메타 정보 조회
	 * 
	 * @param accessToken OAuth2 Access Token (Google)
	 * @param playlistId  플레이리스트 ID
	 */
	public JsonNode getPlaylistById(String accessToken, String playlistId) {
		String url = "https://www.googleapis.com/youtube/v3/playlists" + "?part=snippet,contentDetails" + "&id="
				+ playlistId;

		HttpHeaders h = new HttpHeaders();
		h.setBearerAuth(accessToken);
		ResponseEntity<String> r = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(h), String.class);
		try {
			return mapper.readTree(r.getBody());
		} catch (Exception e) {
			throw new RuntimeException("플레이리스트 메타 조회 실패", e);
		}
	}

	/**
	 * 플레이리스트 아이템(영상) 목록 조회
	 * 
	 * @param accessToken OAuth2 Access Token (Google)
	 * @param playlistId  플레이리스트 ID
	 * @param maxResults  1~50
	 * @param pageToken   null 가능 (다음 페이지용)
	 */
	public JsonNode listPlaylistItems(String accessToken, String playlistId, int maxResults, String pageToken) {
		StringBuilder sb = new StringBuilder("https://www.googleapis.com/youtube/v3/playlistItems")
				.append("?part=snippet,contentDetails").append("&playlistId=").append(playlistId).append("&maxResults=")
				.append(Math.min(Math.max(maxResults, 1), 50));
		if (pageToken != null && !pageToken.isEmpty()) {
			sb.append("&pageToken=").append(pageToken);
		}

		HttpHeaders h = new HttpHeaders();
		h.setBearerAuth(accessToken);
		ResponseEntity<String> r = restTemplate.exchange(sb.toString(), HttpMethod.GET, new HttpEntity<>(h),
				String.class);
		try {
			return mapper.readTree(r.getBody());
		} catch (Exception e) {
			throw new RuntimeException("플레이리스트 아이템 조회 실패", e);
		}
	}

	/**
	 * 여러 비디오의 상세 정보 조회
	 * 
	 * @param accessToken OAuth2 Access Token (Google)
	 * @param ids         비디오 ID 리스트
	 */
	public JsonNode getVideosByIds(String accessToken, List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return mapper.createObjectNode().putArray("items");
		}
		String idParam = String.join(",", ids);
		String url = "https://www.googleapis.com/youtube/v3/videos" + "?part=snippet,contentDetails,statistics" + "&id="
				+ idParam;

		HttpHeaders h = new HttpHeaders();
		h.setBearerAuth(accessToken);
		ResponseEntity<String> r = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(h), String.class);
		try {
			return mapper.readTree(r.getBody());
		} catch (Exception e) {
			throw new RuntimeException("비디오 상세 조회 실패", e);
		}
	}

	@SuppressWarnings("deprecation")
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
