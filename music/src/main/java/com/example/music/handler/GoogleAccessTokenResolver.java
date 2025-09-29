package com.example.music.handler;

import java.time.Instant;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.music.model.GoogleToken;
import com.example.music.service.api.SocialApiClient;

@Component
public class GoogleAccessTokenResolver {

	@Autowired
	SocialApiClient socialApiClient;

	public String getValidAccessToken(HttpSession session) {
		String access = (String) session.getAttribute("googleAccessToken");
		String refresh = (String) session.getAttribute("googleRefreshToken");
		Instant expAt = (Instant) session.getAttribute("googleAccessTokenExpiresAt");

		boolean expired = (expAt == null) || Instant.now().isAfter(expAt.minusSeconds(60));
		if (!expired && access != null) {
			return access;
		}

		if (refresh == null) {
			return null;
		}

		GoogleToken renewed = socialApiClient.refreshGoogleAccessToken(refresh);
		if (renewed == null || renewed.getAccessToken() == null) {
			return null;
		}

		session.setAttribute("googleAccessToken", renewed.getAccessToken());
		if (renewed.getExpiresIn() != null) {
			session.setAttribute("googleAccessTokenExpiresAt", Instant.now().plusSeconds(renewed.getExpiresIn()));
		}

		return renewed.getAccessToken();
	}
}
