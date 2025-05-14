package com.example.music.service;

import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;

import java.util.List;

public interface PlaylistService {
    String addVideoToPlayList(Video video, String playlistId);
    void addPlayList(Playlist playList);
    void updatePlayList(Playlist playList);
    void deletePlaylistByPlaylistId(String playlistId);
    void deleteVideoFromPlayList(PlaylistVideo pv);
    List<Video> getVideosByPlaylistId(String playlistId);
    Playlist getPlaylistByPlaylistId(String playlistId);
    List<Playlist> getPlaylistsWithLastThumbnail(String userId);
    List<Playlist> getPlayListByTitle(String title);
    List<Playlist> getAllPlaylists();
    List<Playlist> getPlaylistsByUserId(String userId);
}
