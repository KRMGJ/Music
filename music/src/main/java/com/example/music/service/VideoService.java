package com.example.music.service;

import com.example.music.model.Video;

import java.util.List;

public interface VideoService {
    void insertVideo(Video video);
    void updateVideo(String videoId);
    void deleteVideo(String id);
    List<Video> getAllVideos();
    List<Video> getVideosByChannel(String channelTitle);
}
