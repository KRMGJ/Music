<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>${playlist.title}-플레이리스트</title>
<link rel="stylesheet" href="/resources/css/playlist/detail.css">
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="playlist-container"
		style="max-width: 1200px; margin: 20px auto;">
		<!-- 헤더 -->
		<div class="pl-header">
			<div class="pl-cover">
				<c:choose>
					<c:when test="${not empty playlist.coverImageUrl}">
						<img src="${playlist.coverImageUrl}" alt="${playlist.title}">
					</c:when>
					<c:otherwise>
						<div style="width: 100%; height: 100%; background: #ddd;"></div>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="pl-meta">
				<h1 class="pl-title">${playlist.title}</h1>
				<div class="pl-sub">
					${playlist.ownerChannelTitle} · 영상 ${playlist.itemCount}개
					<c:if test="${not empty playlist.publishedAt}"> · 생성일 ${playlist.publishedAt}</c:if>
				</div>
				<c:if test="${not empty playlist.description}">
					<div class="pl-desc">${playlist.description}</div>
				</c:if>

				<div class="pl-actions">
					<a class="btn-pill"
						href="https://www.youtube.com/playlist?list=${playlist.id}"
						target="_blank" rel="noopener">▶ 유튜브에서 재생</a>
					<button class="btn-pill" id="btnSharePl"
						data-url="https://www.youtube.com/playlist?list=${playlist.id}">🔗
						공유</button>
				</div>
			</div>
		</div>

		<!-- 아이템 목록 -->
		<div class="playlist-right" style="width: 100%;">
			<c:choose>
				<c:when test="${empty items}">
					<div class="message">영상이 없습니다.</div>
				</c:when>
				<c:otherwise>
					<c:forEach var="v" items="${items}">
						<div class="video-card">
							<div class="video-thumbnail-container">
								<a href="/youtube/${v.id}"> <img class="video-thumbnail"
									src="${v.thumbnailUrl}" alt="${v.title}"> <c:if
										test="${not empty v.formattedDuration}">
										<span class="video-duration">${v.formattedDuration}</span>
									</c:if>
								</a>
							</div>
							<div class="video-info">
								<div class="video-title-container">
									<a class="video-title" href="/video/${v.id}">${v.title}</a>
								</div>
								<div class="video-meta">
									<c:if test="${not empty v.channelTitle}">${v.channelTitle} · </c:if>
									<c:if test="${not empty v.formattedViewCount}">조회수 ${v.formattedViewCount} · </c:if>
									<c:if test="${not empty v.publishedDate}">${v.publishedDate}</c:if>
								</div>
								<div id="descText" class="video-desc js-desc">
									<c:out value="${v.description}" />
								</div>

								<button type="button" id="btnToggleDesc" class="desc-toggle">더보기</button>
							</div>
						</div>
					</c:forEach>

					<!-- 페이지네이션 -->
					<div
						style="display: flex; justify-content: space-between; margin-top: 18px;">
						<c:choose>
							<c:when test="${not empty playlist.prevPageToken}">
								<a class="login-button"
									href="<c:url value='/youtube/playlist/${playlist.id}'>
                     <c:param name='size' value='${size}'/>
                     <c:param name='pageToken' value='${playlist.prevPageToken}'/>
                </c:url>">◀
									이전</a>
							</c:when>
							<c:otherwise>
								<span></span>
							</c:otherwise>
						</c:choose>

						<c:if test="${not empty playlist.nextPageToken}">
							<a class="login-button"
								href="<c:url value='/youtube/playlist/${playlist.id}'>
                   <c:param name='size' value='${size}'/>
                   <c:param name='pageToken' value='${playlist.nextPageToken}'/>
              </c:url>">다음
								▶</a>
						</c:if>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<script>
    document.getElementById('btnSharePl')?.addEventListener('click', function() {
      const url = this.getAttribute('data-url');
      if (navigator.clipboard?.writeText) {
        navigator.clipboard.writeText(url).then(()=>{ this.textContent='복사됨!'; setTimeout(()=>this.textContent='🔗 공유', 1200); });
      } else {
        const temp = document.createElement('input');
        document.body.appendChild(temp);
        temp.value = url; temp.select();
        try { document.execCommand('copy'); this.textContent='복사됨!'; setTimeout(()=>this.textContent='🔗 공유', 1200); }
        catch(e){ alert('복사 실패. 주소창에서 직접 복사해주세요.'); }
        temp.remove();
      }
    });
  </script>
</body>
</html>
