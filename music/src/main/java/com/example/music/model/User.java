package com.example.music.model;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String password;
	private String nickname;
	private String email;
	private String profileImage;
	private String role;
	private String provider;
	private Timestamp createdDate;
	private Timestamp updatedDate;
}
