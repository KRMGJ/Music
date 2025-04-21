package com.example.music.service;

import com.example.music.model.SearchList;
import com.example.music.model.Video;

import java.util.List;

public interface YoutubeService {
    SearchList searchVideos(String query, String channel, int page, String filter, String sort);
    boolean isShortVideo(String title, String description, int durationSec);
    List<Video> paginate(List<Video> videos, int page, int pageSize);
}
