<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>error</title>
</head>
<body>
	<script>
		alert("${message}");
		<c:choose>
			<c:when test="${not empty redirectUrl}">
				window.location.replace("<c:url value='${redirectUrl}'/>");
			</c:when>
			<c:otherwise>
				history.back();
			</c:otherwise>
		</c:choose>
	</script>
</body>
</html>