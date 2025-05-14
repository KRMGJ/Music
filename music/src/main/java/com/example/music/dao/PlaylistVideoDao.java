package com.example.music.dao;

import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;

import java.util.List;

public interface PlaylistVideoDao {
    void insert(String playlistId, String videoId);
    Playlist getPlaylistByPlaylistId(String playlistId);
    List<String> getVideosByPlaylistId(String playlistId);
    void deleteVideoFromPlaylist(PlaylistVideo pv);
}
