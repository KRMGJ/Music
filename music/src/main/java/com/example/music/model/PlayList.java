package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayList {
	
	private int id;
	private int userId;
	private String title;
	private String image;
	private int viewCount;
	private int likeCount;
	private Timestamp createdDate;
}
