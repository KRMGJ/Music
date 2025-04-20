package com.example.music.model;


import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private int id;
	private String userId;
	private String password;
	private String nickname;
	private String email;
	private String profileImage;
	private String role;
	private Timestamp createdDate;
	private Timestamp updatedDate;
}
