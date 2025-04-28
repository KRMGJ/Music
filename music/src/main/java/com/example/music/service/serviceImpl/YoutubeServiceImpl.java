package com.example.music.service.serviceImpl;

import com.example.music.dao.ChannelInfoDao;
import com.example.music.dao.VideoDao;
import com.example.music.model.ChannelInfo;
import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.service.VideoService;
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

    @Autowired
    VideoDao videodao;

    @Autowired
    VideoService videoService;

    @Autowired
    ChannelInfoDao channelInfoDao;

    @Override
    public SearchList searchVideos(String query, int page, String sort) {

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
                if (item.get("id").has("videoId")) {
                    videoIds.add(item.get("id").get("videoId").asText());
                }
            }

            Map<String, JsonNode> videoDetails = apiClient.fetchVideoDetails(videoIds);

            List<Video> videos = new ArrayList<>();
            for (JsonNode item : items) {
                JsonNode idNode = item.get("id");
                if (idNode == null || !idNode.has("videoId")) continue;

                String videoId = idNode.get("videoId").asText();
                JsonNode snippet = item.get("snippet");
                JsonNode videoDetail = videoDetails.get(videoId);

                if (snippet == null || videoDetail == null ||
                        videoDetail.get("statistics") == null || videoDetail.get("contentDetails") == null) {
                    continue;
                }
                String title = snippet.get("title").asText();
                String description = snippet.get("description").asText();
                String thumbnail = snippet.get("thumbnails").get("medium").get("url").asText();
                Timestamp publishedDate = Timestamp.from(OffsetDateTime.parse(snippet.get("publishedAt").asText()).toInstant());
                long viewCount = videoDetails.get(videoId).get("statistics").get("viewCount").asLong();
                String formattedViewCount = formatViewCount(viewCount);

                String durationStr = videoDetails.get(videoId).get("contentDetails").get("duration").asText();
                int durationSec = (int) Duration.parse(durationStr).getSeconds();
                String formattedDuration = getFormattedDuration(durationSec);


                String channelId = snippet.get("channelId").asText();
                ChannelInfo channelInfo = channelInfoMap.get(channelId);

                videos.add(new Video(videoId, channelId, title, thumbnail, description, durationStr, durationSec, formattedDuration, publishedDate, viewCount, formattedViewCount, channelInfo));
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
            Video existing = videodao.getVideoById(videoId);
            if (existing != null) {
                return existing;
            }

            // 2. API 호출해서 데이터 가져오기
            List<String> ids = Collections.singletonList(videoId);
            Map<String, JsonNode> videoDetails = apiClient.fetchVideoDetails(ids);

            if (!videoDetails.containsKey(videoId)) {
                throw new RuntimeException("YouTube API에서 해당 영상을 찾을 수 없습니다.");
            }

            JsonNode detail = videoDetails.get(videoId);
            JsonNode snippet = apiClient.fetchSnippetById(videoId);

            // 3. 데이터 추출
            String title = snippet.get("title").asText();
            String description = snippet.get("description").asText();
            String thumbnail = snippet.get("thumbnails").get("medium").get("url").asText();
            Timestamp publishedDate = Timestamp.from(OffsetDateTime.parse(snippet.get("publishedAt").asText()).toInstant());

            long viewCount = detail.get("statistics").get("viewCount").asLong();
            String formattedViewCount = formatViewCount(viewCount);

            String durationStr = detail.get("contentDetails").get("duration").asText();
            int durationSec = (int) Duration.parse(durationStr).getSeconds();
            String formattedDuration = getFormattedDuration(durationSec);

            String channelId = snippet.get("channelId").asText();
            JsonNode channelData = apiClient.fetchChannelInfo(channelId);
            String channelTitle = channelData.get("snippet").get("title").asText();
            String channelThumb = channelData.get("snippet").get("thumbnails").get("medium").get("url").asText();
            long subscriberCount = channelData.get("statistics").get("subscriberCount").asLong();
            ChannelInfo channelInfo = new ChannelInfo(channelId, channelTitle, channelThumb, subscriberCount);

            Video video = new Video(videoId, channelId, title, thumbnail, description, durationStr, durationSec, formattedDuration, publishedDate, viewCount, formattedViewCount, channelInfo);

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
