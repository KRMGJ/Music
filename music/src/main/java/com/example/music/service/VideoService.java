package com.example.music.service;

import com.example.music.model.Video;

import java.util.List;

public interface VideoService {
    void updateVideo(Video video);
    void deleteVideo(String id);
    Video getVideoById(String id);
    List<Video> getAllVideos();
    List<Video> getVideosByChannel(String channelTitle);
    void saveVideoToPlaylist(Video video, Long playlistId);
}
