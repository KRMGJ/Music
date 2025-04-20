package com.example.music.service.serviceImpl;

import com.example.music.model.Video;
import com.example.music.service.YoutubeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class YoutubeServiceImpl implements YoutubeService {

    private static final String API_KEY = "AIzaSyBBNgTGf8f93anD6oYRWSFBJe388DBXBQg"; // ← 여기 본인 키 입력
    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    @Override
    public List<Video> searchVideos(String query, String channel, int page) {
        List<Video> resultList = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            JsonNode items = callYoutubeApiAndGetItems(encodedQuery);

            List<String> videoIds = new ArrayList<>();

            for (JsonNode item : items) {
                videoIds.add(item.get("id").get("videoId").asText());
            }

            // 상세정보 조회
            Map<String, JsonNode> videoDetails = fetchVideoDetails(videoIds);

            for (JsonNode item : items) {
                String videoId = item.get("id").get("videoId").asText();
                String title = item.get("snippet").get("title").asText();
                String description = item.get("snippet").get("description").asText();
                String thumbnailUrl = item.get("snippet").get("thumbnails").get("high").get("url").asText();
                String channelTitle = item.get("snippet").get("channelTitle").asText();
                OffsetDateTime publishedAt = OffsetDateTime.parse(item.get("snippet").get("publishedAt").asText());
                Timestamp publishedDate = Timestamp.from(publishedAt.toInstant());

                String duration = videoDetails.get(videoId).get("contentDetails").get("duration").asText();
                int durationSec = parseDuration(duration);

                boolean isShorts = (durationSec <= 60) ||
                        title.contains("#shorts") || description.contains("#shorts")
                        || title.contains("#Shorts") || description.contains("#Shorts")
                        || title.contains("#Short") || description.contains("#Short");

                Video video = new Video(videoId, title, thumbnailUrl, channelTitle, isShorts, duration, publishedDate);
                resultList.add(video);
            }

            // 🔹 채널 필터링
            if (channel != null && !channel.isEmpty()) {
                resultList.removeIf(video -> !video.getChannelTitle().equalsIgnoreCase(channel));
            }

            // 🔹 페이징 처리
            int pageSize = 5; // 원하는 페이지당 개수
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, resultList.size());

            if (fromIndex > resultList.size()) return new ArrayList<>(); // 페이지 범위 벗어남

            return resultList.subList(fromIndex, toIndex);
        } catch (Exception exception) {
        	exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static JsonNode callYoutubeApiAndGetItems(String encodedQuery) throws IOException {
        String urlStr = SEARCH_URL
                + "?part=snippet&type=video&maxResults=50&q="
                + encodedQuery + "&key=" + API_KEY;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        reader.close();

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBuilder.toString());
        JsonNode items = root.get("items");
        return items;
    }

    private Map<String, JsonNode> fetchVideoDetails(List<String> videoIds) throws IOException {
        String ids = String.join(",", videoIds);
        String urlStr = "https://www.googleapis.com/youtube/v3/videos"
                + "?part=contentDetails"
                + "&id=" + ids
                + "&key=" + API_KEY;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        reader.close();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBuilder.toString());
        JsonNode items = root.get("items");

        Map<String, JsonNode> detailMap = new HashMap<>();
        for (JsonNode item : items) {
            String id = item.get("id").asText();
            detailMap.put(id, item);
        }

        return detailMap;
    }

    private int parseDuration(String duration) {
        return java.time.Duration.parse(duration).toSecondsPart()
                + java.time.Duration.parse(duration).toMinutesPart() * 60
                + java.time.Duration.parse(duration).toHoursPart() * 3600;
    }
}
