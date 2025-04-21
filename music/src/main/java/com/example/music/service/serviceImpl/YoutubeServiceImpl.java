package com.example.music.service.serviceImpl;

import com.example.music.model.ChannelInfo;
import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.service.YoutubeService;
import com.example.music.service.api.YoutubeApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class YoutubeServiceImpl implements YoutubeService {

    @Autowired
    YoutubeApiClient apiClient;

    @Override
    public SearchList searchVideos(String query, String channel, int page, String filter, String sort) {

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            JsonNode items = apiClient.searchVideos(encodedQuery);

            Set<String> channelIds = new HashSet<>();
            for (JsonNode item : items) {
                String channelId = item.get("snippet").get("channelId").asText();
                channelIds.add(channelId);
            }

            Map<String, ChannelInfo> channelInfoMap = new HashMap<>();
            for (String channelId : channelIds) {
                JsonNode channelData = apiClient.fetchChannelInfo(channelId);
                String title = channelData.get("snippet").get("title").asText();
                String thumbnailUrl = channelData.get("snippet").get("thumbnails").get("default").get("url").asText();
                long subscriberCount = channelData.get("statistics").get("subscriberCount").asLong();
                channelInfoMap.put(channelId, new ChannelInfo(channelId, title, thumbnailUrl, subscriberCount));
            }

            List<String> videoIds = new ArrayList<>();
            for (JsonNode item : items) {
                videoIds.add(item.get("id").get("videoId").asText());
            }

            Map<String, JsonNode> videoDetails = apiClient.fetchVideoDetails(videoIds);

            List<Video> videos = new ArrayList<>();
            for (JsonNode item : items) {
                JsonNode snippet = item.get("snippet");
                String videoId = item.get("id").get("videoId").asText();
                String title = snippet.get("title").asText();
                String description = snippet.get("description").asText();
                String thumbnail = snippet.get("thumbnails").get("medium").get("url").asText();
                Timestamp publishedDate = Timestamp.from(OffsetDateTime.parse(snippet.get("publishedAt").asText()).toInstant());
                long viewCount = videoDetails.get(videoId).get("statistics").get("viewCount").asLong();
                String formattedViewCount = formatViewCount(viewCount);

                String durationStr = videoDetails.get(videoId).get("contentDetails").get("duration").asText();
                int durationSec = (int) Duration.parse(durationStr).getSeconds();
                String formattedDuration = getFormattedDuration(durationSec);
                boolean isShorts = isShortVideo(title, description, durationSec);

                String channelId = snippet.get("channelId").asText();
                ChannelInfo channelInfo = channelInfoMap.get(channelId);

                videos.add(new Video(videoId, title, thumbnail, description, isShorts, durationStr, durationSec, formattedDuration, publishedDate, viewCount, formattedViewCount, channelInfo));
            }

            if (channel != null && !channel.isEmpty()) {
                videos.removeIf(v -> !v.getChannelInfo().getChannelTitle().equalsIgnoreCase(channel));
            }

            // 정렬 처리
            switch (sort) {
                case "oldest":
                    videos.sort(Comparator.comparing(Video::getPublishedDate));
                    break;
                case "length_short":
                    videos.sort(Comparator.comparingInt(Video::getDurationInSeconds)); // 짧은 순
                    break;
                case "length_long":
                    videos.sort(Comparator.comparingInt(Video::getDurationInSeconds).reversed()); // 긴 순
                    break;
                case "title":
                    videos.sort(Comparator.comparing(Video::getTitle));
                    break;
                case "views":
                    videos.sort(Comparator.comparing(Video::getViewCount).reversed());
                    break;
                case "latest":
                    videos.sort(Comparator.comparing(Video::getPublishedDate).reversed());
                    break;
                default:
            }

            // 필터링
            if (filter.equals("shorts")) {
                videos = videos.stream().filter(Video::isShorts).collect(Collectors.toList());
            } else if (filter.equals("videos")) {
                videos = videos.stream().filter(v -> !v.isShorts()).collect(Collectors.toList());
            }


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
    public boolean isShortVideo(String title, String desc, int duration) {
        String t = title.toLowerCase();
        String d = desc.toLowerCase();
        return duration <= 60 || t.contains("#shorts") || d.contains("#shorts") || t.contains("#short") || d.contains("#short");
    }

    @Override
    public List<Video> paginate(List<Video> list, int page, int size) {
        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());
        if (from >= list.size()) return Collections.emptyList();
        return list.subList(from, to);
    }

    public String getFormattedDuration(int durationSec) {
        int hours = durationSec / 3600;
        int minutes = (durationSec % 3600) / 60;
        int remainingSeconds = durationSec % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, remainingSeconds); // 예: 1:02:03
        } else {
            return String.format("%d:%02d", minutes, remainingSeconds); // 예: 2:45
        }
    }

    public String formatViewCount(long viewCount) {
        if (viewCount >= 100_000_000) {
            return (viewCount / 100_000_000) + "억회";
        } else if (viewCount >= 10_000) {
            return (viewCount / 10_000) + "만회";
        } else {
            return String.format("%,d회", viewCount); // 천 단위 콤마
        }
    }

}
