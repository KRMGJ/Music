package com.example.music.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {
	private String videoId;
	private String channelId;
	private String title;
	private String thumbnail;
	private String description;
	private String duration;
	private int durationInSeconds;
	private String formattedDuration;
	private Timestamp publishedDate;
	private long viewCount;
	private String formattedViewCount;
	private ChannelInfo channelInfo; // 채널 정보 추가
	private String channelTitle;
	private String channelThumbnail;
	private long subscriberCount;
}
