package com.example.music.dao;

import com.example.music.model.PlayList;

import java.util.List;

public interface PlayListDao {
    void insert(PlayList playList);
    void update(PlayList playList);
    void delete(int id);
    PlayList getPlaylistById(int id);
    List<PlayList> getAllPlaylists();
    List<PlayList> getPlaylistsByUserId(int userId);
    List<PlayList> getPlaylistsByTitle(String title);
    void incrementViewCount(int id);
    void incrementLikeCount(int id);
    void decrementLikeCount(int id);
}
