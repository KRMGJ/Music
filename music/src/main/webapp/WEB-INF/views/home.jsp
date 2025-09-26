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
<script src="/resources/js/home.js"></script>
</head>
<body>
	<!-- 상단 헤더 -->
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="home-container">
		<section class="section">
			<div class="section-header">
				<h2 class="section-title">피드</h2>
			</div>

			<!-- 탭 버튼 -->
			<div class="feed-tabs">
				<button class="tab-btn active" data-target="tab-popular">인기
					영상</button>
				<button class="tab-btn" data-target="tab-latest">최신 업로드</button>
				<button class="tab-btn" data-target="tab-channels">인기 채널</button>
			</div>

			<!-- 인기 영상 -->
			<div id="tab-popular" class="tab-panel active">
				<div class="video-grid">
					<c:forEach var="v" items="${popularVideos}" varStatus="s">
						<article class="video-card ${s.index >= 6 ? 'hidden-item' : ''}">
							<div class="video-thumbnail-container">
								<a href="/video/${v.id}"> <img class="video-thumbnail"
									src="${v.thumbnail}" alt="${v.title}" /> <c:if
										test="${not empty v.formattedDuration}">
										<span class="video-duration">${v.formattedDuration}</span>
									</c:if>
								</a>
							</div>
							<div class="video-info">
								<a class="video-title" href="/video/${v.id}" title="${v.title}">${v.title}</a>
								<div class="video-meta">
									조회수 ${v.formattedViewCount} ·
									<fmt:formatDate value="${v.publishedDate}" pattern="yyyy.MM.dd" />
								</div>
								<div class="video-channel">
									<img class="channel-thumbnail" src="${v.channelThumbnail}"
										alt="${v.channelTitle}" /> <span title="${v.channelTitle}">${v.channelTitle}</span>
								</div>
								<div class="video-description" title="${v.description}">
									<c:out value="${v.description}" />
								</div>
							</div>
						</article>
					</c:forEach>
				</div>
				<button class="more-inline" data-more="#tab-popular">더보기</button>
			</div>

			<!-- 최신 업로드 -->
			<div id="tab-latest" class="tab-panel">
				<div id="latest-grid" class="video-grid">
					<c:forEach var="v" items="${latestVideos}" varStatus="s">
						<article class="video-card ${s.index >= 6 ? 'hidden-item' : ''}">
							<div class="video-thumbnail-container">
								<a href="/video/${v.id}"> <img class="video-thumbnail"
									src="${v.thumbnail}" alt="${v.title}" /> <c:if
										test="${not empty v.formattedDuration}">
										<span class="video-duration">${v.formattedDuration}</span>
									</c:if>
								</a>
							</div>
							<div class="video-info">
								<a class="video-title" href="/video/${v.id}" title="${v.title}">${v.title}</a>
								<div class="video-meta">
									조회수 ${v.formattedViewCount} ·
									<fmt:formatDate value="${v.publishedDate}" pattern="yyyy.MM.dd" />
								</div>
								<div class="video-channel">
									<img class="channel-thumbnail" src="${v.channelThumbnail}"
										alt="${v.channelTitle}" /> <span title="${v.channelTitle}">${v.channelTitle}</span>
								</div>
								<div class="video-description" title="${v.description}">
									<c:out value="${v.description}" />
								</div>
							</div>
						</article>
					</c:forEach>
				</div>
				<button class="more-inline" data-more="#tab-latest">더보기</button>
			</div>

			<!-- 인기 채널: 가로 스크롤(세로 길이 최소화) -->
			<div id="tab-channels" class="tab-panel">
				<div id="channels-row" class="channel-row"
					style="display: flex; gap: 12px; overflow-x: auto; padding-bottom: 6px;">
					<c:forEach var="ch" items="${popularChannels}">
						<a class="channel-card" href="/channel/${ch.channelId}"
							style="flex: 0 0 240px; background: #fff; border: 1px solid #ddd; border-radius: 12px; padding: 12px; display: flex; gap: 10px; align-items: center;">
							<img src="${ch.channelThumbnail}" alt="${ch.channelTitle}"
							style="width: 48px; height: 48px; border-radius: 50%; object-fit: cover;" />
							<div style="min-width: 0;">
								<div class="channel-title"
									style="font-weight: 600; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
									title="${ch.channelTitle}">${ch.channelTitle}</div>
								<div class="channel-meta" style="font-size: 12px; color: #666;">
									구독자
									<fmt:formatNumber value="${ch.subscriberCount}" type="number" />
								</div>
							</div>
						</a>
					</c:forEach>
				</div>
				<a class="more-inline" href="/channel/list">전체 보기</a>
			</div>
		</section>
	</div>
</body>

</html>
