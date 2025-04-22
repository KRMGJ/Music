<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!-- 🔝 상단 고정 헤더 -->
<header class="bg-white sticky-top shadow-sm py-3" style="z-index: 1030;">
	<div class="container">
		<form method="get" action="/video/search" class="row g-2 align-items-center">
			<div class="col-md-4">
				<input type="text" name="query" placeholder="검색어" class="form-control" value="${param.query}" />
			</div>
			<div class="col-md-3">
				<input type="text" name="channel" placeholder="채널명 (선택)" class="form-control" value="${param.channel}" />
			</div>
			<div class="col-md-2">
				<select name="filter" class="form-select" onchange="this.form.submit()">
					<option value="all" ${param.filter == 'all' ? 'selected' : ''}>전체</option>
					<option value="shorts" ${param.filter == 'shorts' ? 'selected' : ''}>쇼츠</option>
					<option value="videos" ${param.filter == 'videos' ? 'selected' : ''}>일반영상</option>
				</select>
			</div>
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
	</div>
</header>