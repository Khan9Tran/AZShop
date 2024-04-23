<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self' https://maxcdn.bootstrapcdn.com; style-src 'self' https://maxcdn.bootstrapcdn.com https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com https://maxcdn.bootstrapcdn.com">

<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

<title>Electro - HTML Ecommerce Template</title>

<!-- Google font -->
<link
	href="https://fonts.googleapis.com/css?family=Montserrat:400,500,700"
	rel="stylesheet">

<!-- Bootstrap -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- Slick -->
<link href="<c:url value="/templates/guest/css/slick.css" />"
	rel="stylesheet" type="text/css">
<link href="<c:url value="/templates/guest/css/slick-theme.css" />"
	rel="stylesheet" type="text/css">

<!-- nouislider -->
<link href="<c:url value="/templates/guest/css/nouislider.min.css" />"
	rel="stylesheet" type="text/css">

<!-- Font Awesome Icon -->
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">

<!-- Custom stlylesheet -->
<link href="<c:url value="/templates/guest/css/style.css" />"
	rel="stylesheet" type="text/css">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
		  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->
</head>
<body>
	<%@ include file="/common/guest/header.jsp"%>
	<div class="contain">
		<decorator:body></decorator:body>
	</div>
	<%@ include file="/common/guest/footer.jsp"%>


	<!-- jQuery Plugins -->
	<script src="<c:url value="/templates/guest/js/jquery.min.js"></c:url>"></script>
<<<<<<< HEAD
	<!-- JQuery phải được load trước do các tệp js khác có dùng đến nó -->
	
	<script src="<c:url value="/templates/guest/js/slick.min.js"></c:url>"></script>
=======
>>>>>>> 22ec7a95aea185bd99bf846c71de3cd41a08e75f
	<script src="<c:url value="/templates/guest/js/bootstrap.min.js"></c:url>"></script>
	<script src="<c:url value="/templates/guest/js/slick.min.js"></c:url>"></script>	
	<script src="<c:url value="/templates/guest/js/nouislider.min.js"></c:url>"></script>
	<script src="<c:url value="/templates/guest/js/jquery.zoom.min.js"></c:url>"></script>
	<script src="<c:url value="/templates/guest/js/main.js"></c:url>"></script>

</body>
</html>