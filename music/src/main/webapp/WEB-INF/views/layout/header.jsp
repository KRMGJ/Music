<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<header class="bg-white sticky-top shadow-sm py-3"
	style="z-index: 1030; transition: all 0.3s ease;">
	<div class="container">
		<div class="row g-2 align-items-center">
			<!-- 로고 -->
			<div>
				<a href="/playlist/list" class="col-md-2 text-center"> <img
					src="/resources/images/logo.png" alt="Logo"
					class="img-fluid logo-shake" style="max-width: 150px;" />
				</a>
			</div>

			<!-- 검색창 -->
			<form method="get" action="/video/search"
				class="col-md-8 row g-2 align-items-center search-form">
				<div class="col-md-4">
					<input type="text" name="query" placeholder="검색어"
						class="form-control" value="${param.query}" />
				</div>
				<div class="col-md-3">
					<select name="sort" class="form-select">
						<option value="relevance"
							${param.sort == 'relevance' ? 'selected' : ''}>관련도순</option>
						<option value="latest" ${param.sort == 'latest' ? 'selected' : ''}>최신순</option>
						<option value="oldest" ${param.sort == 'oldest' ? 'selected' : ''}>오래된순</option>
						<option value="length_short"
							${param.sort == 'length_short' ? 'selected' : ''}>짧은순</option>
						<option value="length_long"
							${param.sort == 'length_long' ? 'selected' : ''}>긴순</option>
						<option value="views" ${param.sort == 'views' ? 'selected' : ''}>조회수순</option>
						<option value="title" ${param.sort == 'title' ? 'selected' : ''}>이름순</option>
					</select>
				</div>
				<div class="col-md-2 text-center">
					<button type="submit" class="btn btn-light">
						<i class="fas fa-search"></i>
						<!-- 돋보기 아이콘 -->
					</button>
				</div>
			</form>

			<!-- 로그인/로그아웃 -->
			<div class="col-md-2 text-end">
				<c:choose>
					<c:when test="${not empty sessionScope.loginUser}">
						<button id="logoutButton" class="btn btn-outline-secondary">로그아웃</button>
					</c:when>
					<c:otherwise>
						<a href="/auth/login" class="btn btn-outline-secondary me-1">로그인</a>
						<a href="/user/signUp" class="btn btn-outline-primary">회원가입</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</header>
<link rel="stylesheet" href="/resources/css/layout/header.css" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<script src="/resources/js/layout/header.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
	crossorigin="anonymous" referrerpolicy="no-referrer" />


