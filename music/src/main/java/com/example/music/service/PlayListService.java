package com.example.music.service;

import com.example.music.model.PlayList;
import com.example.music.model.Video;

import java.util.List;

public interface PlayListService {
    void addVideoToPlayList(Video video, int playlistId);
    void addPlayList(PlayList playList);
    void updatePlayList(PlayList playList);
    void deletePlayList(int id);
    PlayList getPlayListById(int id);
    List<PlayList> getPlayListByTitle(String title);
    List<PlayList> getAllPlaylists();
    List<PlayList> getPlaylistsByUserId(int userId);
}
