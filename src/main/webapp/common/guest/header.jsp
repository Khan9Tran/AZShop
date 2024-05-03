<%@page import="com.azshop.utils.CSRF"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<header>
	<!-- TOP HEADER -->
	<div id="top-header">
		<div class="container">
			<ul class="header-links pull-left">
				<li><a href="#"><i class="fa fa-phone"></i> +021-95-51-84</a></li>
				<li><a href="#"><i class="fa fa-envelope-o"></i>
						email@email.com</a></li>
				<li><a href="#"><i class="fa fa-map-marker"></i> 1734
						Stonecoal Road</a></li>
			</ul>
			<ul class="header-links pull-right">
				<li><a href="<c:url value='/login-customer' />"> <i
						class="fa fa-user-o"></i> Tài khoản
				</a></li>
			</ul>

		</div>
	</div>
	<!-- /TOP HEADER -->
	<!-- MAIN HEADER -->
	<div id="header">
		<!-- container -->
		<div class="container">
			<!-- row -->
			<div class="row">
				<!-- LOGO -->
				<div class="col-md-3">
					<div class="header-logo">
						<a href="#" class="logo"> <img src="" alt="">
						</a>
					</div>
				</div>
				<!-- /LOGO -->

				<div class="col-md-6">
					<div class="header-search">
						<%
						String csrfToken = CSRF.getToken();

						javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("csrf", csrfToken);
						response.addCookie(cookie);
						%>
						<form action='<c:url value="/guest/search"/>' method="GET"
							accept-charset="UTF-8">
							<input type="hidden" name="csrfToken" value="<%=csrfToken%>" />

							<select class="input-select" name="categoryId">
								<c:if test="${categoryId != null}">
									<option value="-1">All Categories</option>
								</c:if>
								<c:if test="${categoryId == null}">
									<option value="-1" selected>All Categories</option>
								</c:if>
								<c:forEach var="category" items="${categoryParentList}">
									<c:if test="${categoryId != null && categoryId eq category.id}">
										<option value="${category.id}" selected>${category.name}</option>
									</c:if>
									<c:if
										test="${categoryId == null || !(categoryId eq category.id)}">
										<option value="${category.id}">${category.name}</option>
									</c:if>
								</c:forEach>
								<input type="hidden" name="styleId"
								value=${styleId == null ? '-1'  : styleId}>
							</select> <input class="input" name="searchTerm" placeholder="Search here"
								value=${searchTerm == null ? '' : searchTerm}>
							<button type="submit" class="search-btn">Tìm kiếm</button>
						</form>
					</div>
				</div>
				<!-- /SEARCH BAR -->

				<!-- ACCOUNT -->
				<div class="col-md-3 clearfix">
					<div class="header-ctn">

						<!-- Cart -->
						<div class="dropdown">
							<a href="<c:url value='/login-customer' />"> <i
								class="fa fa-shopping-cart"></i> <span>Your Cart</span>
							</a>
						</div>
						<!-- /Cart -->

						<!-- Menu Toogle -->
						<div class="menu-toggle">
							<a href="#"> <i class="fa fa-bars"></i> <span>Menu</span>
							</a>
						</div>
						<!-- /Menu Toogle -->
					</div>
				</div>
				<!-- /ACCOUNT -->
			</div>
			<!-- row -->
		</div>
		<!-- container -->
	</div>
	<!-- /MAIN HEADER -->

	<!-- NAVIGATION -->
	<nav id="navigation">
		<!-- container -->
		<div class="container">
			<!-- responsive-nav -->
			<div id="responsive-nav">
				<!-- NAV -->
				<ul class="main-nav nav navbar-nav">
					<li><a href='<c:url value="/guest-home"/>'>Trang chủ</a></li>
					<li><a href='<c:url value="/guest/hot-product"/>'>Sản phẩm
							hot</a></li>
					<c:forEach var="category" items="${categoryParentList}">
						<li><a
							href='<c:url value="/guest/category/${category.slug}?sortBy=${0}"/>'>${category.name}</a></li>
					</c:forEach>
				</ul>
				<!-- /NAV -->
			</div>
			<!-- /responsive-nav -->
		</div>
		<!-- /container -->
	</nav>
	<!-- /NAVIGATION -->
</header>