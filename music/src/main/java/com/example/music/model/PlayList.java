package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PlayList {
	
	private int id;
	private int userId;
	private String title;
	private String image;
	private int viewCount;
	private int likeCount;
	private Timestamp createdDate;
}
