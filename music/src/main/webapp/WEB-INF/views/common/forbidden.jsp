<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<title>권한 없음</title>
</head>
<body>
	<div class="container">
		<h1>권한 없음</h1>
		<p>이 페이지에 접근할 권한이 없습니다.</p>
		<button class="btn btn-primary" onClick="history.back()">뒤로가기</button>
	</div>
</body>
</html>