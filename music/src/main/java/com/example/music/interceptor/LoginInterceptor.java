package com.example.music.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.music.util.MessageUtil;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
			throws Exception {

		HttpSession session = request.getSession();

		if (session == null) {
			session = request.getSession(true); // true면 없으면 새로 만듦
			MessageUtil.sessionErrorMessage("로그인이 필요합니다.", "/auth/login", session);
			response.sendRedirect("/common/error");
			return false;
		}

        if (session.getAttribute("loginUser") == null) {
            MessageUtil.sessionErrorMessage("로그인이 필요합니다.", "/auth/login", session);
            response.sendRedirect("/common/error");
            return false;
        }
		return true;
	}
}