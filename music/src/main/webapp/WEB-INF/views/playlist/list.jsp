<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<title>내 플레이리스트</title>

<link rel="stylesheet" href="/resources/css/playlist/list.css" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="/resources/js/playlist/list.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="playlist-container">
		
		<div class="list-toolbar">
			<div class="list-actions">
				<strong>내 플레이리스트</strong> <span class="badge">총
					${totalCount}개</span>
			</div>

			<div class="list-actions">
				<!-- 검색 -->
				<form method="get" action="/playlist" id="searchForm">
					<input class="search-input" type="text" name="q" value="${param.q}"
						placeholder="제목으로 검색" />
				</form>

				<!-- 정렬 -->
				<form method="get" action="/playlist" id="sortForm">
					<input type="hidden" name="q" value="${fn:escapeXml(param.q)}" />
					<select class="sort-select" name="sort" id="sortSelect">
						<option value="created_desc"
							${param.sort == 'created_desc' ? 'selected':''}>최근 생성순</option>
						<option value="created_asc"
							${param.sort == 'created_asc'  ? 'selected':''}>오래된순</option>
						<option value="title"
							${param.sort == 'title'        ? 'selected':''}>제목순</option>
						<option value="views"
							${param.sort == 'views'        ? 'selected':''}>조회수순</option>
						<option value="likes"
							${param.sort == 'likes'        ? 'selected':''}>좋아요순</option>
					</select>
				</form>

				<form id="addPlaylistForm" class="create-form">
					<input id="createTitle" name="title" type="text"
						placeholder="새 플레이리스트 제목" required />
					<button type="submit" class="btn primary">생성</button>
				</form>
			</div>
		</div>

		<c:choose>
			<c:when test="${empty playlists}">
				<div class="empty-state">
					아직 플레이리스트가 없어요.<br /> 제목을 입력하고 <b>생성</b>을 눌러 시작해 보세요!
				</div>
			</c:when>
			<c:otherwise>
				<div class="playlist-grid">
					<c:forEach var="pl" items="${playlists}">
						<div class="playlist-card" data-pl-id="${pl.id}">
							<a href="/playlist/videos?playlistId=${pl.id}" class="thumb-link">
								<div class="thumbnail-wrapper">
									<img
										src="${pl.lastVideoThumbnail != null ? pl.lastVideoThumbnail : '/resources/images/default-video-thumbnail.jpg'}"
										alt="${pl.title}" /> <span class="video-count">${pl.videoCount}개
										영상</span>
								</div>
							</a>

							<div class="playlist-info">
								<div class="playlist-title" title="${pl.title}">
									<a href="/playlist/${pl.id}" class="title-link">${pl.title}</a>
								</div>
								<div class="playlist-date">
									<fmt:formatDate value="${pl.createdDate}" pattern="yyyy.MM.dd" />
									생성
								</div>

								<div class="card-actions">
									<a class="view-all-link" href="/playlist/${pl.id}">전체 보기 →</a>
									<button class="btn ghost js-rename">이름 수정</button>
									<button class="btn ghost js-share">공유</button>
									<button class="btn danger js-delete">삭제</button>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>

				<!-- 페이지네이션 -->
				<c:if test="${totalPages > 1}">
					<div class="pagination">
						<c:forEach var="p" begin="1" end="${totalPages}">
							<a
								href="/playlist?page=${p}&q=${fn:escapeXml(param.q)}&sort=${param.sort}"
								class="btn ${p == page ? 'primary' : 'ghost'}">${p}</a>
						</c:forEach>
					</div>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>
