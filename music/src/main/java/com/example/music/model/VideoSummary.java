package com.example.music.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoSummary {
	private String id;
	private String title;
	private String thumbnail;
	private String description;

	private String formattedDuration; // "10:21" 등
	private Long viewCount;
	private String formattedViewCount; // "123만회" 등
	private Date publishedDate;

	private String channelId;
	private String channelTitle;
	private String channelThumbnail;
}