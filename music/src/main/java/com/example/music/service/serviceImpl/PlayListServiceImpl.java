package com.example.music.service.serviceImpl;

import com.example.music.dao.PlayListDao;
import com.example.music.dao.PlayListVideoDao;
import com.example.music.dao.VideoDao;
import com.example.music.model.PlayList;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;
import com.example.music.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.service.PlayListService;

import java.util.Collections;
import java.util.List;

@Service
public class PlayListServiceImpl implements PlayListService {

    @Autowired
    PlayListDao playListDao;

    @Autowired
    VideoDao videoDao;

    @Autowired
    PlayListVideoDao playListVideoDao;

    @Autowired
    VideoService videoService;

    @Override
    public String addVideoToPlayList(Video video, int playlistId) {
        // 1. videos 테이블에 없으면 저장
        Video existing = videoDao.getVideoById(video.getVideoId());
        if (existing == null) {
            videoService.insertVideo(video);
        }
        // 2. playlist_videos 테이블에 매핑 추가
        List<String> videoIds = playListVideoDao.getVideosByPlaylistId(playlistId);
        if (videoIds != null && videoIds.contains(video.getVideoId())) {
            return "error";
        }
        playListVideoDao.insert(playlistId, video.getVideoId());
        return "success";
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
    public void deletePlaylistByPlaylistId(int id) {
        playListDao.delete(id);
    }

    @Override
    public void deleteVideoFromPlayList(PlaylistVideo pv) {
        playListVideoDao.deleteVideoFromPlaylist(pv);
    }

    @Override
    public List<Video> getVideosByPlaylistId(int playlistId) {
        List<String> videoIds = playListVideoDao.getVideosByPlaylistId(playlistId);
        System.out.println("videoIds: " + videoIds);
        if (videoIds != null && !videoIds.isEmpty()) {
            List<Video> videos = videoDao.getVideosByVideoIds(videoIds);
            return videos;
        }
        return Collections.emptyList();
    }

    @Override
    public PlayList getPlaylistByPlaylistId(int playlistId) {
        PlayList playList = playListDao.getPlaylistByPlaylistId(playlistId);
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
