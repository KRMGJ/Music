package com.example.music.service;

import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;

import java.util.List;

public interface PlaylistService {
    String addVideoToPlayList(Video video, int playlistId);
    void addPlayList(Playlist playList);
    void updatePlayList(Playlist playList);
    void deletePlaylistByPlaylistId(int playlistId);
    void deleteVideoFromPlayList(PlaylistVideo pv);
    List<Video> getVideosByPlaylistId(int playlistId);
    Playlist getPlaylistByPlaylistId(int playlistId);
    List<Playlist> getPlaylistsWithLastThumbnail(int userId);
    List<Playlist> getPlayListByTitle(String title);
    List<Playlist> getAllPlaylists();
    List<Playlist> getPlaylistsByUserId(int userId);
}
