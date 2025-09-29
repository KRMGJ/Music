package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentPostRequest {
	private String videoId;
	private String channelId;
	private String text;
	private String parentId;
}