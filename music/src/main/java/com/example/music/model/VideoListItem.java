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
public class VideoListItem {
	private String id;
	private String title;
	private String thumbnail;
	private String channelTitle;
	private String formattedViewCount;
	private String publishedDate; // "yyyy.MM.dd"
	private String formattedDuration;
}
