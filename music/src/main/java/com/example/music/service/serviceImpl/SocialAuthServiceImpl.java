package com.example.music.service.serviceImpl;

import com.example.music.model.*;
import com.example.music.service.SocialAuthService;
import com.example.music.service.api.SocialApiClient;
import com.example.music.util.OAuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.music.service.api.SocialApiClient.*;

@Service
public class SocialAuthServiceImpl implements SocialAuthService {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    SocialApiClient oAuthProperties;

    @Override
    public User googleLogin(String code) {
        String accessToken = oAuthProperties.getAccessTokenFromGoogle(code);

        Map<String, Object> userAttributes = oAuthProperties.getUserInfoFromGoogle(accessToken);
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(userAttributes);

        User user = userService.getUserByEmail(googleUserInfo.getEmail());
        if (user == null) {
            user = new User();
            user.setEmail(googleUserInfo.getEmail());
            user.setNickname(googleUserInfo.getName());
            user.setPassword(UUID.randomUUID().toString()); // 임시 비밀번호
            user.setProfileImage(googleUserInfo.getPicture());
            user.setProvider("google");
            userService.addSocialUser(user);
        }

        return user;
    }

    @Override
    public User naverLogin(String code) {
        String accessToken = oAuthProperties.getAccessTokenFromNaver(code);
        Map<String, Object> userAttributes = oAuthProperties.getUserInfoFromNaver(accessToken);
        NaverUserInfo naverUserInfo = new NaverUserInfo(userAttributes);

        User user = userService.getUserByEmail(naverUserInfo.getEmail());
        if (user == null) {
            user = new User();
            user.setEmail(naverUserInfo.getEmail());
            user.setNickname(naverUserInfo.getNickname());
            user.setPassword(UUID.randomUUID().toString());
            user.setProfileImage(naverUserInfo.getProfileImage());
            user.setProvider("naver");
            userService.addSocialUser(user);
        }
        return user;
    }

    @Override
    public User kakaoLogin(String code) {
        String accessToken = oAuthProperties.getAccessTokenFromKakao(code);
        Map<String, Object> userAttributes = oAuthProperties.getUserInfoFromKakao(accessToken);
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(userAttributes);

        User user = userService.getUserByEmail(kakaoUserInfo.getEmail());
        if (user == null) {
            user = new User();
            user.setEmail(kakaoUserInfo.getEmail());
            user.setNickname(kakaoUserInfo.getNickname());
            user.setPassword(UUID.randomUUID().toString());
            user.setProfileImage(kakaoUserInfo.getProfileImage());
            user.setProvider("kakao");
            userService.addSocialUser(user);
        }
        return user;
    }
}
