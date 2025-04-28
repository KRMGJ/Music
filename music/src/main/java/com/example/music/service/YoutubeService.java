package com.example.music.service;

import com.example.music.model.SearchList;
import com.example.music.model.Video;

import java.util.List;

public interface YoutubeService {
    SearchList searchVideos(String query, int page, String sort);
    Video fetchAndSaveVideoById(String videoId);
    List<Video> paginate(List<Video> videos, int page, int pageSize);
}
