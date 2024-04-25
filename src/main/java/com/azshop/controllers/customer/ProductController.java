package com.azshop.controllers.customer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.azshop.models.CartItemModel;
import com.azshop.models.CartModel;
import com.azshop.models.CategoryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.OrderItemModel;
import com.azshop.models.OrderModel;
import com.azshop.models.ProductModel;
import com.azshop.models.ReviewModel;
import com.azshop.models.StoreModel;
import com.azshop.models.StyleValueModel;
import com.azshop.models.UserModel;
import com.azshop.services.CartItemServiceImpl;
import com.azshop.services.CartServiceImpl;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.ICartItemService;
import com.azshop.services.ICartService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IImageService;
import com.azshop.services.IOrderItemService;
import com.azshop.services.IOrderService;
import com.azshop.services.IProductService;
import com.azshop.services.IReviewService;
import com.azshop.services.IStoreService;
import com.azshop.services.IStyleService;
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.OrderItemServiceImpl;
import com.azshop.services.OrderServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.ReviewServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;

@WebServlet(urlPatterns = { "/customer/product/*", "/guest/product/*", "/review-product" })
public class ProductController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	ICategoryService categoryService = new CategoryServiceImpl();
	IProductService productService = new ProductServiceImpl();
	IStyleService styleService = new StyleServiceImpl();
	IStyleValueService styleValueService = new StyleValueImpl();
	IImageService imageService = new ImageServiceImpl();
	IUserService userService = new UserServiceImpl();
	IStoreService storeService = new StoreServiceImpl();
	ICartService cartService = new CartServiceImpl();
	ICartItemService cartItemService = new CartItemServiceImpl();
	IReviewService reviewService = new ReviewServiceImpl();
	IOrderService orderService = new OrderServiceImpl();
	IOrderItemService orderItemService = new OrderItemServiceImpl();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		resp.setHeader("X-Content-Type-Options", "nosniff");
		// Hiển thị menu danh mục
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);

		if (url.contains("customer")) {
			HttpSession sessionCart = req.getSession();
			UserModel userCart = (UserModel) sessionCart.getAttribute(Constant.userSession);	
			
			List<CartModel> cartList = cartService.getByUserId(userCart.getId());
			List<CartItemModel> cartItemList = new ArrayList<CartItemModel>();
			
			//Hiển thị item trong giỏ hàng
			for (CartModel cart : cartList) {
				List<CartItemModel> itemList = cartItemService.getByCartId(cart.getId());
				cartItemList.addAll(itemList);
			}										
			
			//Lấy thông tin danh sách product có trong giỏ hàng
			List<ProductModel> productsInCart = new ArrayList<ProductModel>();
			
			for (CartItemModel cartItem : cartItemList) {
				ProductModel  productInCart = productService.getById(cartItem.getProductId());
				productInCart.setPrice(productInCart.getPrice().setScale(0));
				productsInCart.add(productInCart);
			}
			
			BigDecimal sum = BigDecimal.ZERO;

			for (int i = 0; i < cartItemList.size(); i++) {
			    ProductModel productModel = productService.getById(cartItemList.get(i).getProductId());
			    
			    if (productModel != null) {
			        BigDecimal productPrice = productModel.getPrice();
			        int count = cartItemList.get(i).getCount();
			        
			        sum = sum.add(productPrice.multiply(BigDecimal.valueOf(count))).setScale(0);
			    }
			}

		    req.setAttribute("sumPrice", sum);
			
			List<ImageModel> imageProductsInCart = new ArrayList<ImageModel>();

			for (ProductModel productModel : productsInCart) {
				ImageModel image = imageService.getImage(productModel.getId());
				imageProductsInCart.add(image);
			}
			
			req.setAttribute("role", "customer");
			req.setAttribute("quantity", cartItemList.size());
			req.setAttribute("user", userCart);
			req.setAttribute("imageProductsInCart", imageProductsInCart);	
			req.setAttribute("cartItemList", cartItemList);
			req.setAttribute("productsInCart", productsInCart);	
		}
		
		
		else if (url.contains("guest")) {
			req.setAttribute("role", "guest");
		}

		if (url.contains("/customer/product/") || url.contains("/guest/product/")) {
			try {
				getProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		if (url.contains("guest")) {
			resp.sendRedirect(req.getContextPath() + "/login-customer");
		}
		
		else if (url.contains("review-product")) {
			try {
				postReviewProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void postReviewProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Đặt kiểu và bảng mã cho response
		resp.setContentType("text/html;charset=UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy thông tin người dùng từ session
		HttpSession session = req.getSession();
		UserModel user = (UserModel) session.getAttribute(Constant.userSession);
		int orderId = 0;

		// Lấy thông tin sản phẩm và cửa hàng từ session
		ProductModel product = (ProductModel) session.getAttribute("productsession");
		StoreModel store = (StoreModel) session.getAttribute("storesession");

		// Lấy danh sách đơn hàng của người dùng cho cửa hàng cụ thể
		List<OrderModel> orders = orderService.getByUserIdAndStoreId(user.getId(), store.getId());

		// Duyệt qua danh sách đơn hàng
		for (OrderModel orderModel : orders) {
			// Lấy danh sách các mục đơn hàng cho sản phẩm cụ thể
			List<OrderItemModel> orderItemModels = orderItemService.getByOrderIdAndProductId(orderModel.getId(),
					product.getId());

			// Kiểm tra điều kiện để đảm bảo đánh giá có thể được thêm
			if (orderItemModels != null && "Completed".equals(orderModel.getStatus())) {
				orderId = orderModel.getId();
				String content = req.getParameter("review");
				String start = req.getParameter("rating");
				int rating = (start != null) ? Integer.parseInt(start) : 0;

				// Tạo đối tượng đánh giá và thêm vào cơ sở dữ liệu
				ReviewModel reviewModel = new ReviewModel();
				reviewModel.setUserId(user.getId());
				reviewModel.setStoreId(store.getId());
				reviewModel.setProductId(product.getId());
				reviewModel.setOrderId(orderId);
				reviewModel.setContent(content);
				reviewModel.setRating(rating);
				reviewService.insert(reviewModel);

				// Cập nhật đánh giá trung bình cho sản phẩm
				product.setRating(reviewService.avgRating(product.getId()));
				productService.update(product);
				
				// Đặt thông báo thành công và chuyển hướng trang
				resp.sendRedirect(req.getContextPath() + "/customer/product/" + product.getSlug() + "?done= Riview san pham thanh cong!");
				return;
			}
		}

		// Đặt thông báo lỗi và chuyển hướng trang
		resp.sendRedirect(req.getContextPath() + "/customer/product/" + product.getSlug() + "?done= Riview san pham that bai!");

	}

	private void getProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// Trích xuất slug từ request URL
			String slug = extractSlugFromRequest(req);
			
			String done = req.getParameter("done");
			req.setAttribute("done", done);

			if (slug != null) {
				// Lấy thông tin sản phẩm dựa trên slug
				ProductModel product = productService.getBySlug(slug);

				if (product != null) {
					// Thiết lập các thuộc tính của sản phẩm cho request
					setProductAttributes(req, product);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Chuyển hướng đến trang jsp hiển thị sản phẩm
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/product.jsp");
		rd.forward(req, resp);
	}

	// Phương thức trích xuất slug từ URL
	private String extractSlugFromRequest(HttpServletRequest req) throws URISyntaxException {
		String url = req.getRequestURL().toString();
		URI uri = new URI(url);
		String path = uri.getPath();
		String[] parts = path.split("/");
		return (parts.length > 0) ? parts[parts.length - 1] : null;
	}

	// Phương thức thiết lập các thuộc tính của sản phẩm cho request
	private void setProductAttributes(HttpServletRequest req, ProductModel product) {
		try {
			// Lấy thông tin liên quan đến sản phẩm và thiết lập cho request
			CategoryModel category = categoryService.getById(product.getCategoryId());
			StyleValueModel styleValue = styleValueService.getById(product.getStyleValueId());
			StoreModel store = storeService.getById(product.getStoreId());
			List<ImageModel> imageList = imageService.getByProductId(product.getId());
			List<ReviewModel> reviewModels = reviewService.getByProductId(product.getId());
			List<ProductModel> productRelateds = productService.getByCategoryId(product.getCategoryId());
			List<ImageModel> imageRelateds = getImageRelateds(productRelateds);

			// Lưu thông tin sản phẩm và các thông tin liên quan vào session
			HttpSession session = req.getSession(true);
			session.setAttribute("productsession", product);
			session.setAttribute("storesession", store);

			// Đặt các thuộc tính cho request
			req.setAttribute("product", product);
			req.setAttribute("store", store);
			req.setAttribute("productRelateds", productRelateds);
			req.setAttribute("category", category);
			req.setAttribute("styleValue", styleValue);
			req.setAttribute("imageList", imageList);
			req.setAttribute("imageRelateds", imageRelateds);
			req.setAttribute("review", reviewModels);
			req.setAttribute("countReview", reviewModels.size());
			req.setAttribute("userList", getUserList(reviewModels));

			// Thiết lập thông tin đánh giá sao
			setStarAttributes(req, product.getId(), reviewModels);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Phương thức lấy danh sách ảnh liên quan đến sản phẩm
	private List<ImageModel> getImageRelateds(List<ProductModel> productRelateds) {
		List<ImageModel> imageRelateds = new ArrayList<>();
		for (ProductModel productRelated : productRelateds) {
			ImageModel imageRelate = imageService.getImage(productRelated.getId());
			imageRelateds.add(imageRelate);
		}
		return imageRelateds;
	}

	// Phương thức lấy danh sách người dùng đánh giá sản phẩm
	private List<UserModel> getUserList(List<ReviewModel> reviewModels) {
		List<UserModel> userList = new ArrayList<>();
		for (ReviewModel reviewModel : reviewModels) {
			UserModel userModel = userService.getById(reviewModel.getUserId());
			userList.add(userModel);
		}
		return userList;
	}

	// Phương thức thiết lập các thuộc tính đánh giá sao cho request
	private void setStarAttributes(HttpServletRequest req, Integer productId, List<ReviewModel> reviewModels) {
		for (int rating = 5; rating >= 1; rating--) {
			String countAttrName = "count" + rating + "Star";
			String rateAttrName = "rate" + rating + "Star";

			req.setAttribute(countAttrName, reviewService.countStar(productId, rating));
			req.setAttribute(rateAttrName, countStar(productId, rating, reviewModels.size()));
		}
	}

	// Phương thức tính tỉ lệ đánh giá sao
	private String countStar(Integer productId, int rating, int size) {
		int totalStarReviews = reviewService.countStar(productId, rating);
		double percentage = ((double) totalStarReviews / size) * 100;
		return String.valueOf(percentage);
	}
}
