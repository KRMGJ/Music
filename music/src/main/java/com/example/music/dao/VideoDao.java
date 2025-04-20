package com.example.music.dao;

import com.example.music.model.Video;

import java.util.List;

public interface VideoDao {
    void insertVideo(Video video);
    void updateVideo(Video video);
    void deleteVideo(String id);
    Video getVideoById(String id);
    List<Video> getAllVideos();
    List<Video> getVideosByChannel(String channelTitle);
}
