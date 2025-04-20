<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<title>YouTube 검색</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<style>
.card-title {
	font-size: 1.2rem;
}

.badge-shorts {
	background-color: red;
	color: white;
	font-size: 0.75rem;
	margin-left: 5px;
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
				<option value="latest" ${param.sort == 'relevance' ? 'selected' : ''}>최신순</option>
				<option value="oldest" ${param.sort == 'relevance' ? 'selected' : ''}>오래된순</option>
				<option value="length_short" ${param.sort == 'relevance' ? 'selected' : ''}>짧은순</option>
				<option value="length_long" ${param.sort == 'relevance' ? 'selected' : ''}>긴순</option>
				<option value="views" ${param.sort == 'relevance' ? 'selected' : ''}>조회수순</option>
				<option value="title" ${param.sort == 'relevance' ? 'selected' : ''}>이름순</option>
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
				<div class="card mb-3">
					<div class="row g-0">
						<div class="col-md-4">
							<a href="https://www.youtube.com/watch?v=${video.id}"
								target="_blank"> <img src="${video.thumbnail}"
								class="img-fluid rounded-start" alt="썸네일" />
							</a>
						</div>
						<div class="col-md-8">
							<div class="card-body">
								<h5 class="card-title">
									${video.title}
									<c:if test="${video.shorts}">
										<span class="badge badge-shorts">쇼츠</span>
									</c:if>
								</h5>
								<p class="card-text text-muted mb-1">채널:
									${video.channelTitle}</p>
								<p class="card-text">
									등록일:
									<fmt:formatDate value="${video.publishedDate}"
										pattern="yyyy-MM-dd HH:mm" />
								</p>
								<a href="https://www.youtube.com/watch?v=${video.id}"
									class="btn btn-sm btn-outline-primary" target="_blank">영상
									보기</a>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>

	<!-- ⏩ 페이지 버튼 -->
	<c:if test="${searchResult.totalPages > 1}">
		<div class="d-flex justify-content-center mt-4">
			<c:forEach var="i" begin="1" end="${searchResult.totalPages}">
				<a
					href="/video/search?query=${param.query}&channel=${param.channel}&filter=${param.filter}&page=${i}"
					class="btn btn-outline-secondary mx-1 ${searchResult.currentPage == i ? 'active' : ''}">
					${i} </a>
			</c:forEach>
		</div>
	</c:if>

</body>
</html>
