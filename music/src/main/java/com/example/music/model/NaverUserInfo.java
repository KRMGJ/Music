package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NaverUserInfo {

    private String email;
    private String nickname;
    private String profileImage;

    public NaverUserInfo(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        this.email = (String) response.get("email");
        this.nickname = (String) response.get("nickname");
        this.profileImage = (String) response.get("profile_image");
    }
}
