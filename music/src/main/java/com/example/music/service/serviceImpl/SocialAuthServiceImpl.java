package com.example.music.service.serviceImpl;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.model.GoogleToken;
import com.example.music.model.GoogleUserInfo;
import com.example.music.model.KakaoUserInfo;
import com.example.music.model.NaverUserInfo;
import com.example.music.model.User;
import com.example.music.service.SocialAuthService;
import com.example.music.service.api.SocialApiClient;

@Service
public class SocialAuthServiceImpl implements SocialAuthService {

	@Autowired
	UserServiceImpl userService;

	@Autowired
	SocialApiClient socialApiClient;

	@Override
	public User googleLogin(String code, HttpSession session) {
		GoogleToken token = socialApiClient.exchangeCodeForGoogleToken(code);

		Map<String, Object> userAttributes = socialApiClient.getUserInfoFromGoogle(token.getAccessToken());
		GoogleUserInfo googleUserInfo = new GoogleUserInfo(userAttributes);

		User user = userService.getUserByEmail(googleUserInfo.getEmail());
		if (user == null) {
			user = new User(googleUserInfo.getEmail(), googleUserInfo.getName(), UUID.randomUUID().toString(),
					googleUserInfo.getPicture(), "google");
			userService.addSocialUser(user);
		}
		if (user.getProvider().equals("google")) {
			user.setNickname(googleUserInfo.getName());
			user.setProfileImage(googleUserInfo.getPicture());
			userService.updateUser(user);
		}
		if (user.getProvider().equals("LOCAL")) {
			throw new RuntimeException("This email is already registered with a local account.");
		}

		session.setAttribute("googleAccessToken", token.getAccessToken());
		session.setAttribute("googleRefreshToken", token.getRefreshToken());
		if (token.getExpiresIn() != null) {
			session.setAttribute("googleAccessTokenExpiresAt", Instant.now().plusSeconds(token.getExpiresIn()));
		}

		return user;
	}

	@Override
	public User naverLogin(String code) {
		String accessToken = socialApiClient.getAccessTokenFromNaver(code);
		Map<String, Object> userAttributes = socialApiClient.getUserInfoFromNaver(accessToken);
		NaverUserInfo naverUserInfo = new NaverUserInfo(userAttributes);

		User user = userService.getUserByEmail(naverUserInfo.getEmail());
		if (user == null) {
			user = new User(naverUserInfo.getEmail(), naverUserInfo.getNickname(), UUID.randomUUID().toString(),
					naverUserInfo.getProfileImage(), "naver");
			userService.addSocialUser(user);
		}
		if (user.getProvider().equals("naver")) {
			user.setNickname(naverUserInfo.getNickname());
			user.setProfileImage(naverUserInfo.getProfileImage());
			userService.updateUser(user);
		}
		if (user.getProvider().equals("LOCAL")) {
			throw new RuntimeException("This email is already registered with a local account.");
		}
		return user;
	}

	@Override
	public User kakaoLogin(String code) {
		String accessToken = socialApiClient.getAccessTokenFromKakao(code);
		Map<String, Object> userAttributes = socialApiClient.getUserInfoFromKakao(accessToken);
		KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(userAttributes);

		User user = userService.getUserByEmail(kakaoUserInfo.getEmail());
		if (user == null) {
			user = new User(kakaoUserInfo.getEmail(), kakaoUserInfo.getNickname(), UUID.randomUUID().toString(),
					kakaoUserInfo.getProfileImage(), "kakao");
			userService.addSocialUser(user);
		}
		if (user.getProvider().equals("kakao")) {
			user.setNickname(kakaoUserInfo.getNickname());
			user.setProfileImage(kakaoUserInfo.getProfileImage());
			userService.updateUser(user);
		}
		if (user.getProvider().equals("LOCAL")) {
			throw new RuntimeException("This email is already registered with a local account.");
		}
		return user;
	}
}
