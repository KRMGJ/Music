<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
	<title>Home</title>
</head>
<body>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />
<h1>
	Hello world!  
</h1>
<P>  The time on the server is ${serverTime}. </P>
</body>
</html>
