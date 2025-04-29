package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class KakaoUserInfo {

    private String email;
    private String nickname;
    private String profileImage;

    public KakaoUserInfo(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        this.email = (String) kakaoAccount.get("email");
        this.nickname = (String) profile.get("nickname");
        this.profileImage = (String) profile.get("profile_image_url");
    }
}
