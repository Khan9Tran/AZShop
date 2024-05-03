<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	
	<!-- TẤT CẢ SẢN PHẨM -->
	<!-- SECTION -->
	<div class="section">
		<!-- container -->
		<div class="container">
			<!-- row -->
			<div class="row">

				<!-- section title -->
				<div class="col-md-12">
					<div class="section-title">
						<h3 class="title">Top 10 sản phẩm bán nhiều nhất</h3>
						<div class="section-nav">
							<ul class="section-tab-nav tab-nav">
								<c:forEach var="category" items="${categoryParentList}">
									<li><a
										href='<c:url value="/guest/category/${category.slug}?sortBy=${0}"/>'>${category.name}</a></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
				<!-- /section title -->

				<!-- Products tab & slick -->
				<div class="col-md-12">
					<div class="row">
						<div class="products-tabs">
							<!-- tab -->
							<div id="tab1" class="tab-pane active">
								<div class="products-slick" data-nav="#slick-nav-1">

									<c:forEach var="product" items="${productList}">
										<!-- product -->
										<div class="product">
											<a href='<c:url value="/${role}/product/${product.slug}"/>'>
												<div class="product-img">
													<!-- Use product-specific information -->
													<c:set var="hasImages" value="false" />
													<c:forEach var="image" items="${imageList}">
														<c:if test="${product.id eq image.productId}">
															<img src="/AZShop/image?fname=${image.image}" alt="" />
															<c:set var="hasImages" value="true" />
														</c:if>
													</c:forEach>

													<c:if test="${not hasImages}">
														<!-- Nếu không có hình ảnh, sử dụng hình ảnh mặc định -->
														<img
															src="${pageContext.request.contextPath}/templates/static/none.png"
															alt="" />
													</c:if>
													<div class="product-label">
														<span class="new">HOT</span>
													</div>
												</div>
											</a>
											<div class="product-body">
												<c:forEach var="category" items="${categoryList}">
													<c:if test="${product.categoryId eq category.id}">
														<p class="product-category">${category.name}</p>
													</c:if>
												</c:forEach>
												
												<h3 class="product-name">
													<a href="#">${product.name}</a>
												</h3>
												<h4 class="product-price">
													${product.price} 													
												</h4>
												<div class="product-rating">
													<!-- Your rating logic here -->
												</div>
												<div class="product-btns">
													<button class="add-to-wishlist">
														<i class="fa fa-heart-o"></i><span class="tooltipp">add
															to wishlist</span>
													</button>
													<button class="add-to-compare">
														<i class="fa fa-exchange"></i><span class="tooltipp">add
															to compare</span>
													</button>
													<button class="quick-view">
														<i class="fa fa-eye"></i><span class="tooltipp">quick
															view</span>
													</button>
												</div>
											</div>
											<a href="<c:url value='/${role}/add-to-cart/${product.slug}?count=${1}'/>">
												<div class="add-to-cart">
													<button class="add-to-cart-btn">
														<i class="fa fa-shopping-cart"></i> add to cart
													</button>
												</div>
											</a>
										</div>
										<!-- /product -->
									</c:forEach>

								</div>
								<div id="slick-nav-1" class="products-slick-nav"></div>
							</div>
							<!-- /tab -->
						</div>
					</div>
				</div>
				<!-- Products tab & slick -->
				
			</div>
			<!-- /row -->
		</div>
		<!-- /container -->
	</div>
	<!-- /SECTION -->
	
	<br><br>

</body>
</html>