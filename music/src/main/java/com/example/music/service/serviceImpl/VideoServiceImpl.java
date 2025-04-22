package com.example.music.service.serviceImpl;

import com.example.music.dao.VideoDao;
import com.example.music.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.service.VideoService;

import java.util.List;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    VideoDao videoDao;

    @Override
    public void updateVideo(Video video) {
        videoDao.updateVideo(video);
    }

    @Override
    public void deleteVideo(String id) {
        videoDao.deleteVideo(id);
    }

    @Override
    public Video getVideoById(String id) {
        Video video = videoDao.getVideoById(id);
        return video;
    }

    @Override
    public List<Video> getAllVideos() {
        List<Video> videoList = videoDao.getAllVideos();
        return videoList;
    }

    @Override
    public List<Video> getVideosByChannel(String channelTitle) {
        List<Video> videoList = videoDao.getVideosByChannel(channelTitle);
        return videoList;
    }

    @Override
    public void saveVideoToPlaylist(Video video, Long playlistId) {
        videoDao.insertPlaylistVideo(playlistId, video.getVideoId());
    }
}
