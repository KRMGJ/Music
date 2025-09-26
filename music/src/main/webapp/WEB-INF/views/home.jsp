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
<style>
/* 레이아웃 유틸 */
.home-container {
	max-width: 1280px;
	margin: 0 auto;
	padding: 16px;
}

.section {
	margin: 24px 0 32px;
}

.section-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 12px;
}

.section-title {
	font-size: 1.25rem;
	font-weight: 700;
}

.more-link {
	font-size: .9rem;
	color: #606060;
	text-decoration: none;
}

.more-link:hover {
	text-decoration: underline;
}

/* 영상 그리드 (가로 썸네일 카드) */
.video-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
	gap: 16px;
}

.video-card {
	display: flex;
	background: #fff;
	border-radius: 8px;
	overflow: hidden;
	border: 1px solid #ddd;
}

.video-thumbnail {
	width: 210px;
	height: 120px;
	object-fit: cover;
	margin: 10px;
	border-radius: 8px;
}

.video-info {
	padding: 10px;
	flex: 1;
	display: flex;
	flex-direction: column;
}

.video-title {
	font-weight: 600;
	font-size: 1rem;
	color: #000;
	text-decoration: none;
}

.video-meta {
	font-size: .85rem;
	color: #666;
	margin-top: 4px;
}

.video-description {
	font-size: .9rem;
	color: #606060;
	margin-top: 6px;
	display: -webkit-box;
	-webkit-line-clamp: 1;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: normal;
}

.video-channel {
	display: flex;
	align-items: center;
	margin-top: 6px;
	font-size: .9rem;
}

.channel-thumbnail {
	width: 24px;
	height: 24px;
	border-radius: 50%;
	margin-right: 6px;
	object-fit: cover;
}

.video-duration {
	position: absolute;
	bottom: 6px;
	right: 10px;
	background: rgba(0, 0, 0, .7);
	color: #fff;
	padding: 2px 6px;
	border-radius: 4px;
	font-size: .8rem;
}

.video-thumbnail-container {
	position: relative;
}

/* 플레이리스트 그리드: list.css의 클래스와 호환 */
.playlist-section .playlist-grid {
	grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
}

/* 채널 그리드 */
.channel-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
	gap: 16px;
}

.channel-card {
	background: #fff;
	border: 1px solid #ddd;
	border-radius: 12px;
	padding: 16px;
	display: flex;
	align-items: center;
	gap: 12px;
}

.channel-avatar {
	width: 48px;
	height: 48px;
	border-radius: 50%;
	object-fit: cover;
}

.channel-title {
	font-weight: 600;
}

.channel-meta {
	font-size: .85rem;
	color: #666;
}

/* 로그인 배너 */
.auth-banner {
	background: #f6f5f7;
	border: 1px dashed #ddd;
	border-radius: 12px;
	padding: 16px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.auth-banner .cta {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 8px 14px;
	border-radius: 20px;
	border: 1px solid #d9d9d9;
	text-decoration: none;
}

body {
	background: #f8f9fa;
}
</style>
</head>
<body>
	<!-- 헤더 포함 (검색바/필터) -->
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="home-container">
		<!-- 로그인 배너: 비로그인만 노출 -->
		<c:if test="${empty sessionScope.loginUser}">
			<section class="section">
				<div class="auth-banner">
					<div>
						<div class="section-title">로그인하고 나만의 플레이리스트를 만들어보세요</div>
						<div class="channel-meta">좋아요/최근 본 영상이 홈에 자동으로 추천돼요</div>
					</div>
					<a href="/auth/login" class="cta login-button"> <span
						class="icon">🔐</span> 시작하기
					</a>
				</div>
			</section>
		</c:if>

		<!-- 내 플레이리스트 (로그인 시) -->
		<c:if test="${not empty sessionScope.loginUser}">
			<section class="section playlist-section">
				<div class="section-header">
					<h2 class="section-title">내 플레이리스트</h2>
					<a class="more-link" href="/playlist/list">모두 보기</a>
				</div>
				<div class="playlist-grid">
					<c:forEach var="pl" items="${myPlaylists}">
						<div class="playlist-card">
							<div class="thumbnail-wrapper">
								<img
									src="${empty pl.image ? '/resources/img/placeholder_playlist.jpg' : pl.image}"
									alt="${pl.title}" />
								<div class="video-count">${pl.videoCount}개영상</div>
							</div>
							<div class="playlist-info">
								<div class="playlist-title">${pl.title}</div>
								<div class="playlist-meta">조회수 ${pl.viewCount} · 좋아요
									${pl.likeCount}</div>
								<div class="playlist-date">
									<fmt:formatDate value="${pl.createdDate}" pattern="yyyy.MM.dd" />
								</div>
								<div class="view-all-link">
									<a href="/playlist/${pl.id}">바로가기</a>
								</div>
							</div>
						</div>
					</c:forEach>
					<c:if test="${empty myPlaylists}">
						<div class="channel-meta">아직 플레이리스트가 없어요. 영상을 추가해 보세요!</div>
					</c:if>
				</div>
			</section>
		</c:if>

		<!-- 인기 영상: 조회수 상위 -->
		<section class="section">
			<div class="section-header">
				<h2 class="section-title">인기 영상</h2>
				<a class="more-link" href="/video/search?sort=viewCount,desc">더보기</a>
			</div>
			<div class="video-grid">
				<c:forEach var="v" items="${popularVideos}">
					<article class="video-card">
						<div class="video-thumbnail-container">
							<a href="/video/${v.id}"> <img class="video-thumbnail"
								src="${v.thumbnail}" alt="${v.title}" /> <c:if
									test="${not empty v.formattedDuration}">
									<span class="video-duration">${v.formattedDuration}</span>
								</c:if>
							</a>
						</div>
						<div class="video-info">
							<a class="video-title" href="/video/${v.id}">${v.title}</a>
							<div class="video-meta">
								조회수 ${v.formattedViewCount} ·
								<fmt:formatDate value="${v.publishedDate}" pattern="yyyy.MM.dd" />
							</div>
							<div class="video-channel">
								<img class="channel-thumbnail" src="${v.channelThumbnail}"
									alt="${v.channelTitle}" /> <span>${v.channelTitle}</span>
							</div>
							<div class="video-description">${v.description}</div>
						</div>
					</article>
				</c:forEach>
			</div>
		</section>

		<!-- 최신 영상: 최신 업로드 -->
		<section class="section">
			<div class="section-header">
				<h2 class="section-title">최신 업로드</h2>
				<a class="more-link" href="/video/search?sort=publishedDate,desc">더보기</a>
			</div>
			<div class="video-grid">
				<c:forEach var="v" items="${latestVideos}">
					<article class="video-card">
						<div class="video-thumbnail-container">
							<a href="/video/${v.id}"> <img class="video-thumbnail"
								src="${v.thumbnail}" alt="${v.title}" /> <c:if
									test="${not empty v.formattedDuration}">
									<span class="video-duration">${v.formattedDuration}</span>
								</c:if>
							</a>
						</div>
						<div class="video-info">
							<a class="video-title" href="/video/${v.id}">${v.title}</a>
							<div class="video-meta">
								조회수 ${v.formattedViewCount} ·
								<fmt:formatDate value="${v.publishedDate}" pattern="yyyy.MM.dd" />
							</div>
							<div class="video-channel">
								<img class="channel-thumbnail" src="${v.channelThumbnail}"
									alt="${v.channelTitle}" /> <span>${v.channelTitle}</span>
							</div>
							<div class="video-description">${v.description}</div>
						</div>
					</article>
				</c:forEach>
			</div>
		</section>

		<!-- 인기 플레이리스트 -->
		<section class="section playlist-section">
			<div class="section-header">
				<h2 class="section-title">인기 플레이리스트</h2>
				<a class="more-link" href="/playlist/list?sort=viewCount,desc">더보기</a>
			</div>
			<div class="playlist-grid">
				<c:forEach var="pl" items="${popularPlaylists}">
					<div class="playlist-card">
						<div class="thumbnail-wrapper">
							<a href="/playlist/${pl.id}"> <img
								src="${empty pl.image ? '/resources/img/placeholder_playlist.jpg' : pl.image}"
								alt="${pl.title}" />
								<div class="video-count">${pl.videoCount}개영상</div>
							</a>
						</div>
						<div class="playlist-info">
							<div class="playlist-title">${pl.title}</div>
							<div class="playlist-meta">조회수 ${pl.viewCount} · 좋아요
								${pl.likeCount}</div>
							<div class="playlist-date">
								<fmt:formatDate value="${pl.createdDate}" pattern="yyyy.MM.dd" />
							</div>
							<div class="view-all-link">
								<a href="/playlist/${pl.id}">상세 보기</a>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</section>

		<!-- 인기 채널 모음 -->
		<section class="section">
			<div class="section-header">
				<h2 class="section-title">인기 채널</h2>
				<a class="more-link" href="/channel/list">더보기</a>
			</div>
			<div class="channel-grid">
				<c:forEach var="ch" items="${popularChannels}">
					<a class="channel-card" href="/channel/${ch.channelId}"> <img
						class="channel-avatar" src="${ch.channelThumbnail}"
						alt="${ch.channelTitle}" />
						<div>
							<div class="channel-title">${ch.channelTitle}</div>
							<div class="channel-meta">
								구독자
								<fmt:formatNumber value="${ch.subscriberCount}" type="number" />
							</div>
						</div>
					</a>
				</c:forEach>
			</div>
		</section>

		<!-- Shorts 모아보기 (옵션) -->
		<c:if test="${not empty shortsVideos}">
			<section class="section">
				<div class="section-header">
					<h2 class="section-title">Shorts</h2>
					<a class="more-link" href="/video/search?isShorts=Y">더보기</a>
				</div>
				<div class="video-grid">
					<c:forEach var="v" items="${shortsVideos}">
						<article class="video-card">
							<div class="video-thumbnail-container">
								<a href="/video/${v.id}"> <img class="video-thumbnail"
									src="${v.thumbnail}" alt="${v.title}" /> <span
									class="video-duration">${v.formattedDuration}</span>
								</a>
							</div>
							<div class="video-info">
								<a class="video-title" href="/video/${v.id}">${v.title}</a>
								<div class="video-meta">조회수 ${v.formattedViewCount}</div>
								<div class="video-channel">
									<img class="channel-thumbnail" src="${v.channelThumbnail}"
										alt="${v.channelTitle}" /> <span>${v.channelTitle}</span>
								</div>
							</div>
						</article>
					</c:forEach>
				</div>
			</section>
		</c:if>
	</div>

</body>
</html>
