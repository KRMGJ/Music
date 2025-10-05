package com.example.music.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.music.model.User;
import com.example.music.service.SocialAuthService;
import com.example.music.service.api.SocialApiClient;
import com.example.music.util.OAuthProperties;

@Controller
@RequestMapping("/oauth2")
public class SocialAuthController {

	@Autowired
	OAuthProperties properties;

	@Autowired
	SocialAuthService socialAuthService;

	@Autowired
	SocialApiClient socialApiClient;

	@GetMapping("/login/google")
	public String redirectToGoogleLogin(@RequestParam(value = "redirect", required = false) String redirect,
			HttpSession session) {
		String state = buildState(session, redirect);

		String redirectUri = properties.getGoogleRedirectUri();
		String scope = properties.getGoogleScope();
		String auth = "https://accounts.google.com/o/oauth2/v2/auth" + "?client_id="
				+ enc(properties.getGoogleClientId()) + "&redirect_uri=" + enc(redirectUri) + "&response_type=code"
				+ "&scope=" + enc(scope) + "&access_type=offline" + "&prompt=consent" + "&include_granted_scopes=true"
				+ "&state=" + enc(state);
		return "redirect:" + auth;
	}

	@GetMapping("/callback/google")
	public String googleLogin(@RequestParam("code") String code,
			@RequestParam(value = "state", required = false) String state, HttpSession session) {
		String redirect = "/";
		try {
			if (StringUtils.hasText(state)) {
				redirect = parseAndValidateState(session, state)[0];
			}
			User user = socialAuthService.googleLogin(code, session);
			session.setAttribute("loginUser", user);
			return "redirect:" + redirect;
		} catch (RuntimeException e) {
			return "redirect:/auth/login?error=social";
		}
	}

	@GetMapping("/login/naver")
	public String redirectToNaverLogin(@RequestParam(value = "redirect", required = false) String redirect,
			HttpSession session) {
		String state = buildState(session, redirect);
		String auth = "https://nid.naver.com/oauth2.0/authorize" + "?client_id=" + enc(properties.getNaverClientId())
				+ "&redirect_uri=" + enc(properties.getNaverRedirectUri()) + "&response_type=code" + "&state="
				+ enc(state);
		return "redirect:" + auth;
	}

	@GetMapping("/callback/naver")
	public String naverLogin(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpSession session) {
		String redirect = parseAndValidateState(session, state)[0];
		try {
			User user = socialAuthService.naverLogin(code);
			session.setAttribute("loginUser", user);
			return "redirect:" + redirect;
		} catch (RuntimeException e) {
			return "redirect:/auth/login?error=social";
		}
	}

	@GetMapping("/login/kakao")
	public String redirectToKakaoLogin(@RequestParam(value = "redirect", required = false) String redirect,
			HttpSession session) {
		String state = buildState(session, redirect);
		String scope = properties.getKakaoScope();
		String auth = "https://kauth.kakao.com/oauth/authorize" + "?client_id=" + enc(properties.getKakaoClientId())
				+ "&redirect_uri=" + enc(properties.getKakaoRedirectUri()) + "&response_type=code"
				+ (StringUtils.hasText(scope) ? "&scope=" + enc(scope) : "") + "&state=" + enc(state);
		return "redirect:" + auth;
	}

	@GetMapping("/callback/kakao")
	public String kakaoLogin(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpSession session) {
		String redirect = parseAndValidateState(session, state)[0];
		try {
			User user = socialAuthService.kakaoLogin(code);
			session.setAttribute("loginUser", user);
			return "redirect:" + redirect;
		} catch (RuntimeException e) {
			return "redirect:/auth/login?error=social";
		}
	}

	/* ========== util ========== */

	private String enc(String v) {
		return URLEncoder.encode(v, StandardCharsets.UTF_8);
	}

	private String buildState(HttpSession session, String redirect) {
		String csrf = UUID.randomUUID().toString();
		session.setAttribute("OAUTH2_CSRF", csrf);

		String safeRedirect = (StringUtils.hasText(redirect) && redirect.startsWith("/")) ? redirect : "/";
		String json = "{\"csrf\":\"" + csrf + "\",\"redirect\":\"" + safeRedirect + "\"}";
		return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
	}

	private String[] parseAndValidateState(HttpSession session, String stateB64) {
		try {
			String json = new String(Base64.getUrlDecoder().decode(stateB64), StandardCharsets.UTF_8);
			String csrf = json.replaceAll(".*\"csrf\"\\s*:\\s*\"([^\"]+)\".*", "$1");
			String redirect = json.replaceAll(".*\"redirect\"\\s*:\\s*\"([^\"]+)\".*", "$1");

			String saved = (String) session.getAttribute("OAUTH2_CSRF");
			session.removeAttribute("OAUTH2_CSRF");

			if (!StringUtils.hasText(saved) || !saved.equals(csrf)) {
				throw new IllegalStateException("Invalid CSRF");
			}
			if (!StringUtils.hasText(redirect) || !redirect.startsWith("/")) {
				redirect = "/";
			}

			return new String[] { redirect, csrf };
		} catch (Exception e) {
			return new String[] { "/", "" };
		}
	}
}
