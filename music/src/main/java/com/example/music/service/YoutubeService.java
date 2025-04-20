package com.example.music.service;

import com.example.music.model.Video;

import java.util.List;

public interface YoutubeService {
    List<Video> searchVideos(String query, String channel, int page);
}
