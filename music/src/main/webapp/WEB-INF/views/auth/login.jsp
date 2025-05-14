<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/auth/login.css">

<script src="https://kit.fontawesome.com/cb52f65530.js" crossorigin="anonymous"></script>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<title>로그인</title>
</head>
<body>
	<div class="auth-wrapper">
	    <c:if test="${not empty errorMessage}">
            <script>
                alert("${errorMessage}");
            </script>
        </c:if>
		<div class="auth-container" id="auth-container">
			<div class="auth-form-container auth-sign-up-container">
				<form class="signUp-form" id="signUpForm">
					<h1>Create Account</h1>
					<div class="social-container">
						<a href="/oauth2/login/google" class="social"><img src="https://cdn.simpleicons.org/google/4285F4" width="24"/></a>
						<a href="/oauth2/login/naver" class="social"><img src="https://cdn.simpleicons.org/naver/03C75A" width="24"/></a>
						<a href="/oauth2/login/kakao" class="social"><img src="https://cdn.simpleicons.org/kakao/FFCD00" width="24"/></a>
					</div>
					<span>or use your email for registration</span>
					<div class="custom-form-control">
					    <input type="text" id="nickname" name="nickname" placeholder="Name" />
					    <i class="custom-icon-success fas fa-check-circle"></i>
					    <i class="custom-icon-error fas fa-exclamation-circle"></i>
					    <small class="custom-error-message"></small>
					</div>
					<div class="custom-form-control">
						<input type="email" id="signUpEmail" name="email" placeholder="Email" />
					    <i class="custom-icon-success fas fa-check-circle"></i>
					    <i class="custom-icon-error fas fa-exclamation-circle"></i>
						<small class="custom-error-message"></small>
					</div>
					<div class="custom-form-control">
						<input type="password" id="signUpPassword" name="password" placeholder="Password" />
					    <i class="custom-icon-success fas fa-check-circle"></i>
					    <i class="custom-icon-error fas fa-exclamation-circle"></i>
						<small class="custom-error-message"></small>
					</div>
					<button>Sign Up</button>
				</form>
			</div>

			<div class="auth-form-container auth-sign-in-container">
				<form class="login-form" id="loginForm">
					<h1>Sign in</h1>
					<div class="social-container">
						<a href="/oauth2/login/google" class="social"><img src="https://cdn.simpleicons.org/google/4285F4" width="24"/></a>
						<a href="/oauth2/login/naver" class="social"><img src="https://cdn.simpleicons.org/naver/03C75A" width="24"/></a>
						<a href="/oauth2/login/kakao" class="social"><img src="https://cdn.simpleicons.org/kakao/FFCD00" width="24"/></a>
					</div>
					<span>or use your account</span> 
					<div class="custom-form-control">
						<input type="email" id="loginEmail" placeholder="email" /> 
					    <i class="custom-icon-success fas fa-check-circle"></i>
					    <i class="custom-icon-error fas fa-exclamation-circle"></i>
						<small class="custom-error-message"></small>
					</div>
					<div class="custom-form-control">
						<input type="password" id="loginPassword" placeholder="Password" />
					    <i class="custom-icon-success fas fa-check-circle"></i>
					    <i class="custom-icon-error fas fa-exclamation-circle"></i>
						<small class="custom-error-message"></small>
					</div>
					<a href="#">Forgot your password?</a>
					<button>Sign In</button>
				</form>
			</div>

			<div class="auth-overlay-container">
				<div class="auth-overlay">
					<div class="auth-overlay-panel auth-overlay-left">
						<h1>Welcome Back!</h1>
						<p>To keep connected with us please login with your personal
							info</p>
						<button class="ghost" id="signIn">Sign In</button>
					</div>
					<div class="auth-overlay-panel auth-overlay-right">
						<h1>Hello, Friend!</h1>
						<p>Enter your personal details and start journey with us</p>
						<button class="ghost" id="signUp">Sign Up</button>
					</div>
				</div>
			</div>
		</div>


		<footer class="auth-footer">
			<p>
				Created with <i class="fa fa-heart"></i> by <a target="_blank"
					href="https://florin-pop.com">Florin Pop</a> - Read how I created
				this and how you can join the challenge <a target="_blank"
					href="https://www.florin-pop.com/blog/2019/03/double-slider-sign-in-up-form/">here</a>.
			</p>
		</footer>
		<script src="/resources/js/auth/login.js"></script>
	</div>
</body>
</html>
