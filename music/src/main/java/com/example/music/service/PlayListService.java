package com.example.music.service;

import com.example.music.model.PlayList;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;

import java.util.List;

public interface PlayListService {
    String addVideoToPlayList(Video video, int playlistId);
    void addPlayList(PlayList playList);
    void updatePlayList(PlayList playList);
    void deletePlaylistByPlaylistId(int playlistId);
    void deleteVideoFromPlayList(PlaylistVideo pv);
    List<Video> getVideosByPlaylistId(int playlistId);
    PlayList getPlaylistByPlaylistId(int playlistId);
    List<PlayList> getPlayListByTitle(String title);
    List<PlayList> getAllPlaylists();
    List<PlayList> getPlaylistsByUserId(int userId);
}
