<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
s
<main class="page-content">
	<!--breadcrumb-->
	<div class="page-breadcrumb d-none d-sm-flex align-items-center mb-3">
		<div class="breadcrumb-title pe-3">eCommerce</div>
		<div class="ps-3">
			<nav aria-label="breadcrumb">
				<ol class="breadcrumb mb-0 p-0">
					<li class="breadcrumb-item"><a href="javascript:;"><i
							class="bx bx-home-alt"></i></a></li>
					<li class="breadcrumb-item active" aria-current="page">Orders</li>
				</ol>
			</nav>
		</div>

	</div>
	<!--end breadcrumb-->


	<	<div class="card mt-4">
		<div class="card-body">
			<div class="product-table">
				<div class="table-responsive white-space-nowrap">
					<table class="table align-middle">
						<thead class="table-light">
							<tr>
								<th>Order ID</th>
								<th>Customer</th>
								<th>Store</th>
								<th>Status</th>
								<th>Date</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="order" items="${listOrder}">
								<tr>
									<td>${order.id}</td>

									<c:forEach var="user" items="${listUser}">
										<c:if test="${user.id eq order.userId}">
											<td>${user.lastName}</td>
										</c:if>
									</c:forEach>

									<c:forEach var="store" items="${listStore}">
										<c:if test="${store.id eq order.storeId}">
											<td>${store.name}</td>
										</c:if>
									</c:forEach>

									<td>${order.status}</td>
									<td>${order.updateAt != null ? order.updateAt : order.createAt}</td>
									<td><c:choose>
											<c:when test="${order.status == 'pending Pickup'}">
												<a class="dropdown-item"
													href='<c:url value="/admin/order-edit-status?orderId=${order.id}"/>'>Shipping</a>
											</c:when>
											<c:when test="${order.status == 'shipping'}">
												<a class="dropdown-item"
													href='<c:url value="/admin/order-edit-status?orderId=${order.id}"/>'>Delivered</a>
											</c:when>
											<c:when test="${order.status == 'delivered'}">
												<a class="dropdown-item"
													href='<c:url value="/admin/order-edit-status?orderId=${order.id}"/>'>Completed</a>
											</c:when>
											
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

</main>