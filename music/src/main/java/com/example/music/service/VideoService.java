package com.example.music.service;

import java.util.List;

import com.example.music.model.Video;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;

public interface VideoService {
	void insertVideo(Video video);

	void updateVideo(String videoId);

	void deleteVideo(String id);

	List<Video> getAllVideos();

	List<Video> getVideosByChannel(String channelTitle);

	VideoDetail getDetail(String id);

	List<VideoListItem> getRelated(String id, int limit);

	List<VideoListItem> getRelatedPage(String id, int page, int size);
}
