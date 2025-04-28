<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<title>list</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="container">
		<!-- 플레이리스트 추가모달 열기버튼 -->
		<button type="button" class="btn btn-primary" data-bs-toggle="modal"
			data-bs-target="#addPlaylistModal">Add Playlist</button>

		<!-- 플레이리스트 추가 모달 -->
		<div class="modal fade" id="addPlaylistModal" tabindex="-1"
			aria-labelledby="addPlaylistModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="addPlaylistModalLabel">Add
							Playlist</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<form id="addPlaylistForm">
							<div class="mb-3">
								<label for="title" class="form-label">Title</label> <input
									type="text" class="form-control" id="title" name="title"
									required>
							</div>
							<button type="submit" class="btn btn-primary">Add</button>
						</form>
					</div>
				</div>
			</div>
		</div>


		<table class="table table-striped">
			<thead>
				<tr>
					<th>Playlist ID</th>
					<th>Thumbnail</th>
					<th>Title</th>
					<th>View Count</th>
					<th>Like Count</th>
					<th>Created Date</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="playlist" items="${playlists}">
					<tr>
						<td>${playlist.playlistId}</td>
						<td><img src="${playlist.lastVideoThumbnail}" alt="Thumbnail" width="120"
							height="90">
						<td>${playlist.playlistTitle}</td>
						<td>${playlist.viewCount}</td>
						<td>${playlist.likeCount}</td>
						<td>${playlist.createdDate}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<script src="/resources/js/playlist/list.js"></script>
</body>
</html>