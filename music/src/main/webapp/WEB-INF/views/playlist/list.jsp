<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<title>내 유튜브 플레이리스트</title>
<link rel="stylesheet" href="/resources/css/playlist/list.css">
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="playlist-container">
		<h3 style="margin-bottom: 16px;">재생목록</h3>

		<c:choose>
			<c:when test="${empty items}">
				<div class="message">플레이리스트가 없습니다.</div>
			</c:when>
			<c:otherwise>
				<div class="playlist-grid">
					<c:forEach var="p" items="${items}">
						<div class="playlist-card">
							<div class="thumbnail-wrapper">
								<img src="${p.thumbnailUrl}" alt="${p.title}" />
								<div class="video-count">${p.itemCount}개영상</div>
							</div>
							<div class="playlist-info">
								<div class="playlist-title" title="${p.title}">${p.title}</div>
								<div class="view-all-link">
									<a href="/youtube/playlist/${p.id}">플레이리스트 열기</a>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>

				<!-- 페이지네이션 (SSR) -->
				<div
					style="display: flex; justify-content: space-between; margin-top: 20px;">
					<c:choose>
						<c:when test="${not empty prevPageToken}">
							<a class="login-button"
								href="<c:url value='/youtube/my-playlists'>
                              <c:param name='size' value='${size}'/>
                              <c:param name='pageToken' value='${prevPageToken}'/>
                        </c:url>">
								◀ 이전 </a>
						</c:when>
						<c:otherwise>
							<span></span>
						</c:otherwise>
					</c:choose>

					<c:if test="${not empty nextPageToken}">
						<a class="login-button"
							href="<c:url value='/youtube/my-playlists'>
                          <c:param name='size' value='${size}'/>
                          <c:param name='pageToken' value='${nextPageToken}'/>
                    </c:url>">
							다음 ▶ </a>
					</c:if>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
<script src="/resources/js/playlist/list.js"></script>
</body>
</html>
