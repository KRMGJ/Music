package com.example.music.dao;

import com.example.music.model.Video;

import java.util.List;

public interface PlayListVideoDao {
    void insert(int playlistId, String videoId);
    List<String> getVideosByPlaylistId(int playlistId);
}
