<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Music — Home</title>
<!-- 공용 스타일 -->
<link rel="stylesheet" href="/resources/css/layout/header.css" />
<link rel="stylesheet" href="/resources/css/playlist/list.css" />
<link rel="stylesheet" href="/resources/css/playlist/videos.css" />
<link rel="stylesheet" href="/resources/css/video/searchResult.css" />
<link rel="stylesheet" href="/resources/css/home.css" />
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>
	<!-- 상단 헤더 -->
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="home-container">
		<section class="section">
			<div class="section-header">
				<h2 class="section-title">피드</h2>
			</div>

			<!-- 탭 -->
			<div class="feed-tabs">
				<button class="tab-btn active" data-target="tab-popular">인기
					영상</button>
				<button class="tab-btn" data-target="tab-latest">최신 업로드</button>
				<button class="tab-btn" data-target="tab-channels">인기 채널</button>
			</div>

			<!-- 인기 영상 (AJAX) -->
			<div id="tab-popular" class="tab-panel active">
				<div class="video-grid" id="popular-grid"></div>
				<button class="more-inline" data-more="#tab-popular">더보기</button>
			</div>

			<!-- 최신 업로드 (AJAX) -->
			<div id="tab-latest" class="tab-panel">
				<div class="video-grid" id="latest-grid"></div>
				<button class="more-inline" data-more="#tab-latest">더보기</button>
			</div>

			<!-- 인기 채널 (AJAX, GRID) -->
			<div id="tab-channels" class="tab-panel">
				<div id="channels-grid" class="channel-grid"></div>
				<button class="more-inline" data-more="#tab-channels">더보기</button>
			</div>
		</section>
	</div>

	<!-- ================= Templates ================= -->
	<!-- 비디오 카드 템플릿 -->
	<template id="tpl-video-card">
		<article class="video-card">
			<div class="video-thumbnail-container">
				<a class="video-link" href="#"> <img class="video-thumbnail"
					src="" alt="" /> <span class="video-duration"></span>
				</a>
			</div>
			<div class="video-info">
				<a class="video-title" href="#" title=""></a>
				<div class="video-meta"></div>
				<div class="video-channel">
					<img class="channel-thumbnail" src="" alt="" /> <span title=""></span>
				</div>
				<div class="video-description" title=""></div>
			</div>
		</article>
	</template>

	<!-- 채널 카드 템플릿 -->
	<template id="tpl-channel-card">
		<div class="channel-card">
			<div class="rank-badge">1</div>
			<img class="channel-avatar" src="" alt="" />
			<div class="channel-info">
				<a class="channel-title" href="#" title=""></a>
				<div class="channel-meta"></div>
			</div>
		</div>
	</template>
	<!-- ================================================= -->

	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script src="/resources/js/home.js"></script>
</body>
</html>