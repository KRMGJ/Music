package com.example.music.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class YoutubeApiClient {

    private static final String API_KEY = "AIzaSyBBNgTGf8f93anD6oYRWSFBJe388DBXBQg";
    private static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String DETAILS_URL = "https://www.googleapis.com/youtube/v3/videos";
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonNode searchVideos(String encodedQuery) throws Exception {
        String url = SEARCH_URL + "?part=snippet&type=video&maxResults=50&q=" + encodedQuery + "&key=" + API_KEY;
        return mapper.readTree(sendGetRequest(url)).get("items");
    }

    public Map<String, JsonNode> fetchVideoDetails(List<String> videoIds) throws Exception {
        if (videoIds.isEmpty()) return Collections.emptyMap();

        String idsParam = String.join(",", videoIds);
        String url = DETAILS_URL
                + "?part=contentDetails,statistics"  // ← 여기 statistics 추가
                + "&id=" + idsParam
                + "&key=" + API_KEY;

        JsonNode items = mapper.readTree(sendGetRequest(url)).get("items");

        Map<String, JsonNode> result = new HashMap<>();
        for (JsonNode item : items) {
            result.put(item.get("id").asText(), item);
        }
        return result;
    }

    private String sendGetRequest(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);

            return response.toString();
        }
    }
}
