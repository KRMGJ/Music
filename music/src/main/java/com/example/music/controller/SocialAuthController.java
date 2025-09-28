package com.example.music.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.music.model.User;
import com.example.music.service.SocialAuthService;
import com.example.music.util.OAuthProperties;

@Controller
@RequestMapping("/oauth2")
public class SocialAuthController {

    @Autowired
    OAuthProperties properties;

    @Autowired
    SocialAuthService socialAuthService;

    @GetMapping("/login/google")
    public String redirectToGoogleLogin() {
        String redirectUri = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + properties.getGoogleClientId()
                + "&redirect_uri=" + properties.getGoogleRedirectUri()
                + "&response_type=code"
                + "&scope=" + properties.getGoogleScope()
                + "&access_type=offline";
        return "redirect:" + redirectUri;
    }

    @GetMapping("/callback/google")
    public String googleLogin(@RequestParam("code") String code, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = socialAuthService.googleLogin(code);
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (RuntimeException e) {
        	e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "이미 등록된 이메일입니다.");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/login/naver")
    public String redirectToNaverLogin() {
        String redirectUri = "https://nid.naver.com/oauth2.0/authorize"
                + "?client_id=" + properties.getNaverClientId()
                + "&redirect_uri=" + properties.getNaverRedirectUri()
                + "&response_type=code"
                + "&state=naverLogin";
        return "redirect:" + redirectUri;
    }

    @GetMapping("/callback/naver")
    public String naverLogin(@RequestParam("code") String code, HttpSession session) {
        try{
            User user = socialAuthService.naverLogin(code);
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/login/kakao")
    public String redirectToKakaoLogin() {
        String redirectUri = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + properties.getKakaoClientId()
                + "&redirect_uri=" + properties.getKakaoRedirectUri()
                + "&response_type=code"
                + "&scope=" + properties.getKakaoScope();
        return "redirect:" + redirectUri;
    }


    @GetMapping("/callback/kakao")
    public String kakaoLogin(@RequestParam("code") String code, HttpSession session) {
        try{
            User user = socialAuthService.kakaoLogin(code);
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
}
