package com.example.music.model;

import java.sql.Timestamp;
import java.util.Date;

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
	private String channelTitle;
	private boolean isShorts;
	private String duration;
	private Timestamp publishedDate;
}
