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
	private String id;
	private String title;
	private String thumbnail;
	private String description;
	private boolean isShorts;
	private String duration;
	private int durationInSeconds;
	private String formattedDuration;
	private Timestamp publishedDate;
	private long viewCount;
	private String formattedViewCount;
	private ChannelInfo channelInfo;
}
