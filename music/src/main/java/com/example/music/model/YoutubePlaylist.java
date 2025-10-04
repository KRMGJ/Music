package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubePlaylist {
	private String id;
	private String title;
	private String thumbnailUrl; // 대표 썸네일(기본: default)
	private int itemCount;
}
