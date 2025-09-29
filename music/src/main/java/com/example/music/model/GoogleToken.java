package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleToken {
	private String accessToken;
	private String refreshToken;
	private Long expiresIn; // seconds
	private String tokenType; // "Bearer"
	private String scope;
	private String idToken; // 필요시
}
