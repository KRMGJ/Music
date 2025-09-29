package com.example.music.service;

import javax.servlet.http.HttpSession;

import com.example.music.model.User;

public interface SocialAuthService {
	User googleLogin(String code, HttpSession session);

	User naverLogin(String code);

	User kakaoLogin(String code);
}
