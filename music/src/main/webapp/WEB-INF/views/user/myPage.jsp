<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<title>내 정보</title>
</head>
<body>
	<div class="container">
		<p>아이디: ${user.userId}</p>
		<p>이름: ${user.nickname}</p>
		<p>이메일: ${user.email}</p>

		<a href="<c:url value='/user/updateUser'/>" class="btn btn-primary">정보수정</a>
		<a href="<c:url value='/user/deleteUser'/>" class="btn btn-danger">회원탈퇴</a>
	</div>
</body>
</html>