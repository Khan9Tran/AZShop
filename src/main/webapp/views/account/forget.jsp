<%@page import="com.azshop.utils.CSRF"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Security-Policy" content="">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/templates/account/css/bootstrap.min.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/templates/account/css/fontawesome-all.min.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/templates/account/css/iofrm-style.css" />">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/templates/account/css/iofrm-theme22.css" />">
</head>
<body>
	<div class="form-body without-side">
		<div class="row">
			<div class="img-holder">
				<div class="bg"></div>
				<div class="info-holder">
					<img
						src="<c:url value="/templates/account/img/graphic3.svg"></c:url>"
						alt="">
				</div>
			</div>
			<div class="form-holder">
				<div class="form-content">
					<div class="form-items">
						<%-- Hiển thị thông báo lỗi nếu có --%>
						<%
						String forGetError = (String) request.getAttribute("forGetError");
						%>
						<%
						if (forGetError != null) {
						%>
						<div class="alert alert-danger" role="alert">
							<%=forGetError%>
						</div>
						<%
						}
						%>

						<h3>Password Reset</h3>
						<p>To reset your password, enter the email address you use to
							sign in to iofrm</p>
						<%
						String csrfToken = CSRF.getToken();

						javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("csrf", csrfToken);
						response.addCookie(cookie);
						%>
						<form action="forget-customer" method="post">
							<input type="hidden" name="csrfToken" value="<%=csrfToken%>" />
							
							<input class="form-control" type="text" name="username"
								placeholder="E-mail Address" required>
							<div class="form-button full-width">
								<button id="submit" type="submit" class="ibtn"
									style="background-color: #d21737;">Send Reset Link</button>
							</div>
						</form>
					</div>

				</div>
			</div>
		</div>
	</div>
	<script src="<c:url value='/templates/account/js/jquery.min.js' />"></script>
	<script src="<c:url value='/templates/account/js/popper.min.js' />"></script>
	<script src="<c:url value='/templates/account/js/bootstrap.min.js' />"></script>
	<script src="<c:url value='/templates/account/js/main.js' />"></script>
</body>
</html>