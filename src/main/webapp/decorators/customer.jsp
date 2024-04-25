<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

<title>Electro - HTML Ecommerce Template</title>

<!-- Google font -->
<link
	href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700"
	rel="stylesheet">

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
	integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />

<!-- Bootstrap -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- Slick -->
<link rel="stylesheet" type="text/css"
	href="<c:url value='/templates/guest/css/slick.css' />">
<link rel="stylesheet" type="text/css"
	href="<c:url value='/templates/guest/css/slick-theme.css' />">

<!-- nouislider -->
<link rel="stylesheet" type="text/css"
	href="<c:url value='/templates/guest/css/nouislider.min.css' />">

<!-- Font Awesome Icon -->
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">

<!-- Custom stlylesheet -->
<link rel="stylesheet" type="text/css"
	href="<c:url value='/templates/guest/css/style.css' />">

</head>
<body>

	<%@ include file="/common/customer/header.jsp"%>
	<title><decorator:title></decorator:title></title>
	<decorator:head></decorator:head>
	<div class="contain">
		<decorator:body></decorator:body>
	</div>
	<%@ include file="/common/customer/footer.jsp"%>

	<!-- jQuery Plugins -->
	<script src="<c:url value='/templates/guest/js/jquery.min.js' />"></script>

	<script src="<c:url value='/templates/guest/js/slick.min.js' />"></script>
	<script src="<c:url value='/templates/guest/js/bootstrap.min.js' />"></script>
	<script src="<c:url value='/templates/guest/js/slick.min.js' />"></script>
	<script src="<c:url value='/templates/guest/js/nouislider.min.js' />"></script>
	<script src="<c:url value='/templates/guest/js/jquery.zoom.min.js' />"></script>
	<script src="<c:url value='/templates/guest/js/main.js' />"></script>

</body>
</html>