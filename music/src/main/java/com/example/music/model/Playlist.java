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
public class Playlist {

	private String id;
	private String userId;
	private String nickname;
	private String title;
	private Timestamp createdDate;
	private int playlistId;
	private String playlistTitle;
	private String lastVideoThumbnail;
	private String videoCount;
}
