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

		<div class="list-toolbar">
			<div class="list-actions">
				<select id="sortOption" class="sort-select">
					<option value="latest">최신순</option>
					<option value="oldest">오래된순</option>
					<option value="title">제목순</option>
					<option value="count">영상개수순</option>
				</select> <select id="filterOption" class="sort-select">
					<option value="all">전체</option>
					<option value="public">공개</option>
					<option value="unlisted">일부공개</option>
					<option value="private">비공개</option>
				</select>
			</div>

			<input type="text" id="searchInput" class="search-input"
				placeholder="재생목록 검색">
		</div>

		<c:choose>
			<c:when test="${empty items}">
				<div class="message">플레이리스트가 없습니다.</div>
			</c:when>
			<c:otherwise>
				<div class="playlist-grid">
					<c:forEach var="p" items="${items}">
						<div class="playlist-card" data-title="${playlist.title}"
							data-count="${playlist.videoCount}"
							data-created="${playlist.createdTimestamp}"
							data-privacy="${playlist.privacyStatus}">
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
