<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<title>YouTube 검색</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<style>
.video-duration {
	position: absolute;
	bottom: 5px;
	right: 8px;
	background-color: rgba(0, 0, 0, 0.75);
	color: white;
	font-size: 0.75rem;
	padding: 2px 5px;
	border-radius: 4px;
}

.badge-shorts {
	background-color: red;
	color: white;
	font-size: 0.7rem;
	height: fit-content;
	margin-left: 10px;
}

.video-description {
	font-size: 0.85rem;
	max-height: 2.6em; /* 두 줄 제한 */
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}
</style>

</head>
<body class="container py-4">

	<!-- 🔍 검색 폼 -->
	<form method="get" action="/video/search" class="row g-2 mb-4">
		<div class="col-md-4">
			<input type="text" name="query" placeholder="검색어" class="form-control" value="${param.query}" />
		</div>
		<div class="col-md-3">
			<input type="text" name="channel" placeholder="채널명 (선택)" class="form-control" value="${param.channel}" />
		</div>

		<!-- 필터 선택 -->
		<div class="col-md-2">
			<select name="filter" class="form-select" onchange="this.form.submit()">
				<option value="all" ${param.filter == 'all' ? 'selected' : ''}>전체</option>
				<option value="shorts" ${param.filter == 'shorts' ? 'selected' : ''}>쇼츠</option>
				<option value="videos" ${param.filter == 'videos' ? 'selected' : ''}>일반영상</option>
			</select>
		</div>

		<!-- 정렬 선택 -->
		<div class="col-md-2">
			<select name="sort" class="form-select" onchange="this.form.submit()">
				<option value="relevance" ${param.sort == 'relevance' ? 'selected' : ''}>관련도순</option>
				<option value="latest" ${param.sort == 'latest' ? 'selected' : ''}>최신순</option>
				<option value="oldest" ${param.sort == 'oldest' ? 'selected' : ''}>오래된순</option>
				<option value="length_short" ${param.sort == 'length_short' ? 'selected' : ''}>짧은순</option>
				<option value="length_long" ${param.sort == 'length_long' ? 'selected' : ''}>긴순</option>
				<option value="views" ${param.sort == 'views' ? 'selected' : ''}>조회수순</option>
				<option value="title" ${param.sort == 'title' ? 'selected' : ''}>이름순</option>
			</select>
		</div>
		<div class="col-md-1">
			<button type="submit" class="btn btn-primary">검색</button>
		</div>
	</form>

	<!-- 📺 검색 결과 -->
	<c:choose>
		<c:when test="${empty searchResult.videos}">
			<p class="text-muted">검색 결과가 없습니다.</p>
		</c:when>
		<c:otherwise>
			<c:forEach var="video" items="${searchResult.videos}">
				<div class="d-flex mb-4" style="height: 120px;">
					<!-- 썸네일 -->
					<div class="me-3 position-relative" style="flex-shrink: 0;">
						<a href="https://www.youtube.com/watch?v=${video.id}"
							target="_blank"> <img src="${video.thumbnail}" alt="썸네일"
							style="width: 210px; height: 120px; object-fit: cover; border-radius: 8px;" />
							<div class="video-duration">${video.formattedDuration}</div>
						</a>
					</div>

					<!-- 텍스트 영역 -->
					<div class="flex-grow-1">
						<!-- 제목 -->
						<a href="https://www.youtube.com/watch?v=${video.id}"
							target="_blank" class="text-decoration-none text-dark fw-bold"
							style="font-size: 1rem;"> ${video.title} </a>

						<!-- 조회수, 업로드일 -->
						<div class="text-muted" style="font-size: 0.85rem;">
							조회수 ${video.formattedViewCount} ·
							<fmt:formatDate value="${video.publishedDate}"
								pattern="yyyy년 M월 d일" />
						</div>

						<!-- 채널 정보 -->
						<div class="d-flex align-items-center my-1"
							style="font-size: 0.9rem;">
							<img src="${video.channelInfo.channelThumbnail}" alt="채널썸네일"
								style="width: 24px; height: 24px; border-radius: 50%; object-fit: cover; margin-right: 8px;" />
							<span>${video.channelInfo.channelTitle}</span>
						</div>

						<!-- 설명 -->
						<div class="text-muted video-description">
							${fn:escapeXml(video.description)}</div>
					</div>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>

	<!-- ⏩ 페이지 버튼 -->
	<c:if test="${searchResult.totalPages > 1}">
		<div class="d-flex justify-content-center mt-4">
			<c:forEach var="i" begin="1" end="${searchResult.totalPages}">
				<a href="/video/search?query=${param.query}&channel=${param.channel}&filter=${param.filter}&sort=${param.sort}&page=${i}"
					class="btn btn-outline-secondary mx-1 ${searchResult.currentPage == i ? 'active' : ''}">
					${i} 
				</a>
			</c:forEach>
		</div>
	</c:if>

</body>
</html>
