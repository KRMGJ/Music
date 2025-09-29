package com.example.music.service;

import java.util.List;

import com.example.music.model.Video;

public interface VideoService {
	void insertVideo(Video video);

	void updateVideo(String videoId);

	void deleteVideo(String id);

	List<Video> getAllVideos();
}
