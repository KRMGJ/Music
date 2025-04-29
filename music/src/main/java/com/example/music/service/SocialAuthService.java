package com.example.music.service;

import com.example.music.model.User;

public interface SocialAuthService {
    User googleLogin(String code);
    User naverLogin(String code);
    User kakaoLogin(String code);
}
