<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="header-container">
	<header class="youtube-header">
		<!-- 로고 -->
		<div class="left-section">
			<a href="/" class="youtube-logo"> <img
				src="<c:url value='/resources/images/youtube-logo.svg' />"
				alt="YouTube Logo" />
			</a>
		</div>

		<!-- 검색창 -->
		<div class="center-section">
			<form method="get" action="/video/search" class="search-form">
				<input type="text" name="query" placeholder="검색"
					value="${param.query}" />
				<button type="submit">
					<i class="fas fa-search"></i>
				</button>
				<label class="region-only"> <input type="checkbox"
					name="regionCode" value="KR"
					${param.regionCode == 'KR' ? 'checked' : ''} /> <span>국내
						영상만 보기</span>
				</label>
			</form>
		</div>

		<!-- 필터 버튼 -->
		<div class="filter-btn-wrapper">
			<button id="filterBtn" class="filter-btn">
				<i class="fas fa-sliders-h"></i> 필터
			</button>
		</div>

		<!-- 로그인 영역 -->
		<div class="right-section">
			<c:choose>
				<c:when test="${not empty sessionScope.loginUser}">
						<button id="userMenuToggle" class="user-chip" aria-haspopup="true"
							aria-expanded="false">
							<img class="avatar"
								src="${empty sessionScope.loginUser.profileImage ? '/resources/images/default-avatar.png' : sessionScope.loginUser.profileImage}"
								alt="profile" /> <span class="name">${empty sessionScope.loginUser.nickname ? sessionScope.loginUser.email : sessionScope.loginUser.nickname}</span>
							<span class="caret">▾</span>
						</button>

						<div id="userDropdown" class="user-dropdown" role="menu">
							<a class="item" href="/youtube/my-playlists">📂 내 플레이리스트</a>

							
							<button type="button" id="logoutButton" class="item danger">로그아웃</button>
						</div>
				</c:when>

				<c:otherwise>
					<a class="login-button" href="/auth/login"> <span class="icon">🔑</span>
						로그인
					</a>
				</c:otherwise>
			</c:choose>
		</div>
	</header>
</div>

<!-- 필터 모달 -->
<div class="filter-modal" id="filterModal">
	<div class="filter-modal-content">
		<form method="get" action="/video/search">
			<input type="hidden" name="query" value="${param.query}" />
			<c:set var="uploadKeys" value="1h,today,week,month,year" />
			<c:set var="uploadValues" value="1시간,오늘,이번 주,이번 달,올해" />

			<div class="filter-section">
				<h4>업로드 날짜</h4>
				<c:forEach var="key" items="${fn:split(uploadKeys, ',')}"
					varStatus="status">
					<label> <input type="radio" name="upload" value="${key}"
						${param.upload == key ? 'checked' : ''}
						onclick="toggleRadio(this)" /> <span>${fn:split(uploadValues, ',')[status.index]}</span>
					</label>
				</c:forEach>
			</div>

			<div class="filter-section">
				<h4>길이</h4>
				<label><input type="radio" name="duration" value="short"
					${param.duration == 'short' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>4분 미만</span></label> <label><input
					type="radio" name="duration" value="medium"
					${param.duration == 'medium' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>4~20분</span></label> <label><input
					type="radio" name="duration" value="long"
					${param.duration == 'long' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>20분 초과</span></label>
			</div>

			<div class="filter-section">
				<h4>정렬 기준</h4>
				<label><input type="radio" name="sort" value="relevance"
					${param.sort == 'relevance' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>관련성</span></label> <label><input
					type="radio" name="sort" value="latest"
					${param.sort == 'latest' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>최신</span></label> <label><input
					type="radio" name="sort" value="oldest"
					${param.sort == 'oldest' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>과거</span></label> <label><input
					type="radio" name="sort" value="views"
					${param.sort == 'views' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>조회수</span></label> <label><input
					type="radio" name="sort" value="rating"
					${param.sort == 'rating' ? 'checked' : ''}
					onclick="toggleRadio(this)"><span>평점</span></label>
			</div>

			<div class="filter-action">
				<button type="submit" class="btn btn-primary">적용</button>
				<span class="close-btn" id="closeModal">&times;</span>
			</div>
		</form>
	</div>
</div>


<link rel="stylesheet" href="/resources/css/layout/header.css" />
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="/resources/js/layout/header.js"></script>
