package com.example.music.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String password;
	private String nickname;
	private String email;
	private String profileImage;
	private String role;
	private String provider;
	private Timestamp createdDate;
	private Timestamp updatedDate;

	public User(String email, String nickname, String password, String profileImage, String provider) {
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.profileImage = profileImage;
		this.provider = provider;
	}
}
