package com.example.music.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.dao.VideoDao;
import com.example.music.model.Video;
import com.example.music.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	VideoDao videoDao;

	@Override
	public void insertVideo(Video video) {
		Video existingVideo = videoDao.getVideoById(video.getVideoId());
		if (existingVideo != null) {
			videoDao.updateVideo(video.getVideoId());
			return;
		}
		videoDao.insertVideo(video);
	}

	@Override
	public void updateVideo(String videoId) {
		videoDao.updateVideo(videoId);
	}

	@Override
	public void deleteVideo(String id) {
		videoDao.deleteVideo(id);
	}

	@Override
	public List<Video> getAllVideos() {
		List<Video> videoList = videoDao.getAllVideos();
		return videoList;
	}
}