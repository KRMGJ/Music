package com.example.music.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.music.util.MessageUtil;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
	        throws Exception {
	    HttpSession session = request.getSession(false);

		if (session == null) {
			session = request.getSession(true); // true면 없으면 새로 만듦
			MessageUtil.sessionErrorMessage("로그인이 필요합니다.", "/auth/login", session);
			response.sendRedirect("/common/error");
			return false;
		}

		if (session.getAttribute("loginUser") == null) {
	        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
	        if (Boolean.TRUE.equals(isAdmin)) {
	            return true;
	        }
		    response.sendRedirect("/common/forbidden"); // 권한 없음
		    return false;

	    }
		MessageUtil.sessionErrorMessage("로그인이 필요합니다.", "/auth/login", session);
	    response.sendRedirect("/common/error");
	    return false;
	}
}
