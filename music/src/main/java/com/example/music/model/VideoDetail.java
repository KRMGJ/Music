package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDetail {
	private String id;
	private String title;
	private String description;

	private String channelId;
	private String channelTitle;
	private String channelThumbnail;
	private String formattedSubscriberCount;

	private String formattedViewCount;
	private String publishedDate;
	private String thumbnail;
	private String formattedDuration;
}