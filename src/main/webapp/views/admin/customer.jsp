<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Customer</title>
</head>
<body>
	<!--start main content-->
	<main class="page-content">
		<!--breadcrumb-->
		<div class="page-breadcrumb d-none d-sm-flex align-items-center mb-3">
			<div class="breadcrumb-title pe-3">eCommerce</div>
			<div class="ps-3">
				<nav aria-label="breadcrumb">
					<ol class="breadcrumb mb-0 p-0">
						<li class="breadcrumb-item"><a href="javascript:;"><i
								class="bx bx-home-alt"></i></a></li>
						<li class="breadcrumb-item active" aria-current="page">Products</li>
					</ol>
				</nav>
			</div>
			<div class="ms-auto">
				<div class="btn-group">
					<button type="button" class="btn btn-primary">Settings</button>
					<button type="button"
						class="btn btn-primary split-bg-primary dropdown-toggle dropdown-toggle-split"
						data-bs-toggle="dropdown">
						<span class="visually-hidden">Toggle Dropdown</span>
					</button>
					<div class="dropdown-menu dropdown-menu-right dropdown-menu-lg-end">
						<a class="dropdown-item" href="javascript:;">Action</a> <a
							class="dropdown-item" href="javascript:;">Another action</a> <a
							class="dropdown-item" href="javascript:;">Something else here</a>
						<div class="dropdown-divider"></div>
						<a class="dropdown-item" href="javascript:;">Separated link</a>
					</div>
				</div>
			</div>
		</div>
		<!--end breadcrumb-->


		<div
			class="product-count d-flex align-items-center gap-3 gap-lg-4 mb-4 fw-bold flex-wrap font-text1">
			<a href="javascript:;"><span class="me-1">All</span><span
				class="text-secondary">(85472)</span></a> <a href="javascript:;"><span
				class="me-1">New</span><span class="text-secondary">(145)</span></a> <a
				href="javascript:;"><span class="me-1">Checkouts</span><span
				class="text-secondary">(89)</span></a> <a href="javascript:;"><span
				class="me-1">Locals</span><span class="text-secondary">(5872)</span></a>
			<a href="javascript:;"><span class="me-1">Subscribers</span><span
				class="text-secondary">(163)</span></a> <a href="javascript:;"><span
				class="me-1">Top Reviews</span><span class="text-secondary">(8)</span></a>
		</div>

		<div class="row g-3">
			<div class="col-auto">
				<div class="position-relative">
					<input class="form-control px-5" type="search"
						placeholder="Search Customers"> <span
						class="material-symbols-outlined position-absolute ms-3 translate-middle-y start-0 top-50 fs-5">search</span>
				</div>
			</div>
			<div class="col-auto flex-grow-1 overflow-auto">
				<div class="btn-group position-static">
					<div class="btn-group position-static">
						<button type="button"
							class="btn border btn-light dropdown-toggle px-4"
							data-bs-toggle="dropdown" aria-expanded="false">Country
						</button>
						<ul class="dropdown-menu">
							<li><a class="dropdown-item" href="javascript:;">Action</a></li>
							<li><a class="dropdown-item" href="javascript:;">Another
									action</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="javascript:;">Something
									else here</a></li>
						</ul>
					</div>
					<div class="btn-group position-static">
						<button type="button"
							class="btn border btn-light dropdown-toggle px-4"
							data-bs-toggle="dropdown" aria-expanded="false">Source</button>
						<ul class="dropdown-menu">
							<li><a class="dropdown-item" href="javascript:;">Action</a></li>
							<li><a class="dropdown-item" href="javascript:;">Another
									action</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="javascript:;">Something
									else here</a></li>
						</ul>
					</div>
					<div class="btn-group position-static">
						<button type="button"
							class="btn border btn-light dropdown-toggle px-4"
							data-bs-toggle="dropdown" aria-expanded="false">More
							Filters</button>
						<ul class="dropdown-menu">
							<li><a class="dropdown-item" href="javascript:;">Action</a></li>
							<li><a class="dropdown-item" href="javascript:;">Another
									action</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="javascript:;">Something
									else here</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="col-auto">
				<div class="d-flex align-items-center gap-2 justify-content-lg-end">
					<button class="btn btn-light px-4">
						<i class="bi bi-box-arrow-right me-2"></i>Export
					</button>
					<button class="btn btn-primary px-4">
						<i class="bi bi-plus-lg me-2"></i>Add Customers
					</button>
				</div>
			</div>
		</div>
		<!--end row-->

		<div class="card mt-4">
			<div class="card-body">
				<div class="customer-table">
					<div class="table-responsive white-space-nowrap">
						<table id="example2" class="table align-middle">
							<thead class="table-light">
								<tr>
									<th><input class="form-check-input" type="checkbox">
									</th>
									<th>Customers</th>
									<th>Email</th>
									<th>Phone</th>
									<th>Role</th>
									<th>Active Email</th>
									<th>Active Phone</th>
									<th>Point</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="user" items="${listuser}">
									<tr>
										<td><input class="form-check-input" type="checkbox">
										</td>
										<td><a class="d-flex align-items-center gap-3"
											href="javascript:;">
												<div class="customer-pic">
													<img src="${user.coverImage }" class="rounded-circle"
														width="40" height="40" alt="">
												</div>
												<p class="mb-0 customer-name fw-bold">${user.firstName }
													${user.lastName }</p>
										</a></td>
										<td><a href="javascript:;" class="font-text1">${user.email }</a>
										</td>
										<td>${user.phone }</td>
										<td>${user.role }</td>
										<td><c:choose>
												<c:when test="${user.emailActive }">
													<span
														class="lable-table bg-success-subtle text-success rounded border border-success-subtle font-text2 fw-bold">Activated</span>

												</c:when>
												<c:otherwise>
													<span
														class="lable-table bg-danger-subtle text-danger rounded border border-danger-subtle font-text2 fw-bold">not
														activated</span>
												</c:otherwise>

											</c:choose></td>
										<td><c:choose>
												<c:when test="${user.phoneActive }">
													<span
														class="lable-table bg-success-subtle text-success rounded border border-success-subtle font-text2 fw-bold">Activated</span>

												</c:when>
												<c:otherwise>
													<span
														class="lable-table bg-danger-subtle text-danger rounded border border-danger-subtle font-text2 fw-bold">not
														activated</span>

												</c:otherwise>

											</c:choose></td>
										<td>${user.point }</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

	</main>
	<!--end main content-->

</body>
</html>