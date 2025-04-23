package com.example.music.dao;

import com.example.music.model.Playlist;

import java.util.List;

public interface PlaylistDao {
    void insert(Playlist playList);
    void update(Playlist playList);
    void delete(int playlistId);
    Playlist getPlaylistByPlaylistId(int playlistId);
    List<Playlist> getAllPlaylists();
    List<Playlist> getPlaylistsByUserId(int userId);
    List<Playlist> getPlaylistsByTitle(String title);
    void incrementViewCount(int id);
    void incrementLikeCount(int id);
    void decrementLikeCount(int id);
}
