<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>${video.title}</title>

<meta property="og:type" content="video.other" />
<meta property="og:title" content="${video.title}" />
<meta property="og:description" content="${video.description}" />
<meta property="og:image" content="${video.thumbnail}" />
<meta property="og:url" content="${pageContext.request.requestURL}" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="/resources/css/video/video.css" />
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="page-wrap">
		<div class="video-layout">
			<!-- 메인 -->
			<main>
				<!-- 플레이어 -->
				<div class="player-wrap">
					<iframe src="https://www.youtube.com/embed/${video.id}?autoplay=1"
						title="YouTube video player"
						allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
						allowfullscreen frameborder="0" style="width: 100%; height: 100%">
					</iframe>
				</div>

				<!-- 제목 -->
				<h1 class="video-title-xl">${video.title}</h1>

				<!-- 액션 -->
				<div class="video-actions">
					<button class="chip" id="btnLike">👍 좋아요</button>
					<button class="chip" id="btnSave" data-video="${video.id}">📁
						플레이리스트</button>
					<button class="chip" id="btnShare">🔗 공유</button>
					<span class="chip text-muted">조회수
						${video.formattedViewCount} · ${video.publishedDate}</span>
				</div>

				<!-- 채널 -->
				<div class="channel-box">
					<img class="channel-avatar" src="${video.channelThumbnail}"
						alt="${video.channelTitle}" />
					<div class="flex-grow-1">
						<div style="font-weight: 700">${video.channelTitle}</div>
						<div class="text-muted" style="font-size: 12px;">구독자
							${video.formattedSubscriberCount}</div>
					</div>
					<button class="btn btn-dark btn-sm btn-sub">구독</button>
				</div>

				<!-- 설명 -->
				<div class="desc-wrap">
					<div id="descText" class="desc-text">${video.description}</div>
					<div id="btnToggleDesc" class="toggle-more mt-2 text-primary">더보기</div>
				</div>
			</main>

			<!-- 추천 영상 -->
			<aside>
				<div class="aside-title">추천 영상</div>
				<div id="relList">
					<c:forEach var="r" items="${related}">
						<a class="rel-item text-decoration-none text-reset"
							href="/video/${r.id}">
							<div class="rel-thumb">
								<img src="${r.thumbnail}" alt="${r.title}" />
								<c:if test="${not empty r.formattedDuration}">
									<span class="rel-dur">${r.formattedDuration}</span>
								</c:if>
							</div>
							<div>
								<div
									style="font-weight: 600; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
									${r.title}</div>
								<div class="rel-meta">${r.channelTitle}</div>
								<div class="rel-meta">조회수 ${r.formattedViewCount} ·
									${r.publishedDate}</div>
							</div>
						</a>
					</c:forEach>
				</div>
				<!-- 더보기 버튼 -->
				<button class="btn btn-light w-100 mt-2" id="btnMore" data-token="${nextPageToken}">더보기</button>
			</aside>
		</div>
	</div>

	<!-- 모달 -->
	<div class="modal fade" id="playlistModal" tabindex="-1"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">플레이리스트에 저장</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body">
					<input type="hidden" id="modalVideoId" /> 로그인 후 사용 가능합니다.
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
					<button class="btn btn-primary">저장</button>
				</div>
			</div>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="/resources/js/video/video.js"></script>
</body>
</html>
