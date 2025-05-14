package com.example.music.dao;

import com.example.music.model.Playlist;

import java.util.List;

public interface PlaylistDao {
    void insert(Playlist playList);
    void update(Playlist playList);
    void delete(String playlistId);
    Playlist getPlaylistByPlaylistId(String playlistId);
    List<Playlist> getAllPlaylists();
    List<Playlist> getPlaylistsByUserId(String userId);
    List<Playlist> getPlaylistsByTitle(String title);
    List<Playlist> getPlaylistsWithLastThumbnailByUserId(String userId);
    void incrementViewCount(String id);
    void incrementLikeCount(String id);
    void decrementLikeCount(String id);
}
