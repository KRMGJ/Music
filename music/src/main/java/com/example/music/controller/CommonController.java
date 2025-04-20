package com.example.music.controller;

import com.example.music.util.MessageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/common")
public class CommonController {

    @RequestMapping("/forbidden")
    public String forbidden() {
        return "common/forbidden";
    }

    @RequestMapping("/error")
    public String error(HttpSession session, Model model) {
        String errorMessage = (String) session.getAttribute("errorMessage");
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        MessageUtil.errorMessage(errorMessage, redirectUrl, model);
        return "common/error";
    }
    
    @RequestMapping("/404")
    public String notFoundPage() {
    	return "common/404";
    }
}
