package com.example.music.service.serviceImpl;

import com.example.music.dao.PlayListDao;
import com.example.music.dao.PlayListVideoDao;
import com.example.music.dao.VideoDao;
import com.example.music.model.PlayList;
import com.example.music.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.service.PlayListService;

import java.util.List;

@Service
public class PlayListServiceImpl implements PlayListService {

    @Autowired
    PlayListDao playListDao;

    @Autowired
    VideoDao videoDao;

    @Autowired
    PlayListVideoDao playListVideoDao;

    @Override
    public void addVideoToPlayList(Video video, int playlistId) {
        // 1. videos 테이블에 없으면 저장
        Video existing = videoDao.getVideoById(video.getVideoId());
        if (existing == null) {
            videoDao.insertVideo(video);
        }
        // 2. playlist_videos 테이블에 매핑 추가
        playListVideoDao.insert(playlistId, video.getVideoId());
    }

    @Override
    public void addPlayList(PlayList playList) {
        playListDao.insert(playList);
    }

    @Override
    public void updatePlayList(PlayList playList) {
        playListDao.update(playList);
    }

    @Override
    public void deletePlayList(int id) {
        playListDao.delete(id);
    }

    @Override
    public PlayList getPlayListById(int id) {
        PlayList playList = playListDao.getPlaylistById(id);
        return playList;
    }

    @Override
    public List<PlayList> getPlayListByTitle(String title) {
        List<PlayList> playList = playListDao.getPlaylistsByTitle(title);
        return playList;
    }

    @Override
    public List<PlayList> getAllPlaylists() {
        List<PlayList> playList = playListDao.getAllPlaylists();
        return playList;
    }

    @Override
    public List<PlayList> getPlaylistsByUserId(int userId) {
        List<PlayList> playList = playListDao.getPlaylistsByUserId(userId);
        return playList;
    }
}
