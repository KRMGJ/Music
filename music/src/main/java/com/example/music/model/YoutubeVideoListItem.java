package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeVideoListItem {
	private String id;
	private String title;
	private String description;
	private String thumbnailUrl;
	private Integer durationSeconds;
	private String formattedDuration;
	private String channelTitle;
	private String channelThumbnail;
	private String publishedDate;
	private Long viewCount;
	private String formattedViewCount;
}
