package com.example.music.dao;

import java.util.List;

import com.example.music.model.Video;

public interface VideoDao {
	void insertVideo(Video video);

	void updateVideo(String videoId);

	void deleteVideo(String id);

	Video getVideoById(String videoId);

	List<Video> getAllVideos();

	List<Video> getVideosByChannel(String channelTitle);

	void insertPlaylistVideo(Long playlistId, String videoId);

	List<Video> getVideosByVideoIds(List<String> videoIds);
}
