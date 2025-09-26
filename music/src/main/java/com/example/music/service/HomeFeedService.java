package com.example.music.service;

import java.util.List;

import com.example.music.model.ChannelSummary;
import com.example.music.model.VideoSummary;

public interface HomeFeedService {
	List<VideoSummary> getPopularForHome(String regionCode, int limit) throws Exception;

	List<VideoSummary> getLatestForHome(String regionCode, int limit) throws Exception;

	List<ChannelSummary> getPopularChannels(String regionCode, int limit) throws Exception;
}
