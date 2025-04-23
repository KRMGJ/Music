package com.example.music.dao;

import com.example.music.model.PlayList;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;

import java.util.List;

public interface PlayListVideoDao {
    void insert(int playlistId, String videoId);
    PlayList getPlaylistByPlaylistId(int playlistId);
    List<String> getVideosByPlaylistId(int playlistId);
    void deleteVideoFromPlaylist(PlaylistVideo pv);
}
