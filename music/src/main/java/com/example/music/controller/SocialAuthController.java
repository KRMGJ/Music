package com.example.music.controller;

import com.example.music.model.User;
import com.example.music.service.SocialAuthService;
import com.example.music.util.OAuthProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/oauth2")
public class SocialAuthController {

    @Autowired
    OAuthProperties properties;

    @Autowired
    SocialAuthService socialAuthService;

    @GetMapping("/login/google")
    public String googleLogin(@RequestParam("code") String code, HttpSession session) {
        User user = socialAuthService.googleLogin(code);
        session.setAttribute("loginUser", user);
        return "redirect:/";
    }

    @GetMapping("/login/naver")
    public String naverLogin(@RequestParam("code") String code, HttpSession session) {
        User user = socialAuthService.naverLogin(code);
        session.setAttribute("loginUser", user);
        return "redirect:/";
    }

    @GetMapping("/login/kakao")
    public String redirectToKakaoLogin() {
        String redirectUri = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + properties.getKakaoClientId()
                + "&redirect_uri=" + properties.getKakaoRedirectUri()
                + "&response_type=code"
                + "&scope=profile_nickname, account_email, profile_image";
        return "redirect:" + redirectUri;
    }


    @GetMapping("/callback/kakao")
    public String kakaoLogin(@RequestParam("code") String code, HttpSession session) {
        User user = socialAuthService.kakaoLogin(code);
        session.setAttribute("loginUser", user);
        return "redirect:/";
    }
}
