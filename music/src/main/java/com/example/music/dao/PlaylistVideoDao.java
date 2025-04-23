package com.example.music.dao;

import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;

import java.util.List;

public interface PlaylistVideoDao {
    void insert(int playlistId, String videoId);
    Playlist getPlaylistByPlaylistId(int playlistId);
    List<String> getVideosByPlaylistId(int playlistId);
    void deleteVideoFromPlaylist(PlaylistVideo pv);
}
