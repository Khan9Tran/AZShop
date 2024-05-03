package com.azshop.controllers.vendor;

import java.awt.Image;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle.Control;

import javax.mail.Message;
import javax.mail.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.swing.tree.DefaultTreeCellEditor.EditorContainer;

import com.azshop.DAO.IOrderDAO;
import com.azshop.DAO.IReviewDAO;
import com.azshop.DAO.IUserFollowStoreDAO;
import com.azshop.DAO.UserDAOImpl;
import com.azshop.models.CategoryModel;
import com.azshop.models.DeliveryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.OrderItemModel;
import com.azshop.models.OrderModel;
import com.azshop.models.ProductModel;
import com.azshop.models.RevenueData;
import com.azshop.models.ReviewModel;
import com.azshop.models.StoreModel;
import com.azshop.models.StyleModel;
import com.azshop.models.StyleValueModel;
import com.azshop.models.UserModel;
import com.azshop.services.AddressShippingServiceImpl;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.DeliveryServiceImpl;
import com.azshop.services.IAddressShippingService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IDeliveryService;
import com.azshop.services.IImageService;
import com.azshop.services.IOrderItemService;
import com.azshop.services.IOrderService;
import com.azshop.services.IProductService;
import com.azshop.services.IReviewService;
import com.azshop.services.IStoreService;
import com.azshop.services.IStyleService;
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserFollowStoreService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.OrderItemServiceImpl;
import com.azshop.services.OrderServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.ReviewServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserFollowStoreServiceImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.CheckValid;
import com.azshop.utils.Constant;
import com.azshop.utils.ImageUtil;
import com.azshop.utils.SlugUtil;
import com.azshop.utils.UploadImage;
import com.azshop.utils.UploadUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024
		* 50)
@WebServlet(urlPatterns = { "/vendor/dashboard", "/vendor/update-shop-info", "/vendor/view-shop-info",
		"/vendor/product/new", "/vendor/product/all", "/vendor/product/error404", "/vendor/product/edit/*",
		"/vendor/order/detail/*", "/vendor/order/cancelled", "/vendor/order/all", "/vendor/order/processed",
		"/vendor/order/details", "/vendor/product/delete/*", "/vendor/logout", "/vendor/order/status",
		"/vendor/pickup-address", "/vendor/review", "/vendor/product/error403", "/vendor/statistics-revenue",
		"/vendor/statistics-product", "/vendor/review/detail" })
public class VenderController extends HttpServlet {

	ICategoryService categoryService = new CategoryServiceImpl();
	IStyleService styleService = new StyleServiceImpl();
	IStyleValueService styleValueService = new StyleValueImpl();
	IProductService productService = new ProductServiceImpl();
	IImageService imageService = new ImageServiceImpl();
	IOrderService orderService = new OrderServiceImpl();
	IDeliveryService deliveryService = new DeliveryServiceImpl();
	IOrderItemService orderItemService = new OrderItemServiceImpl();
	IAddressShippingService addressShippingService = new AddressShippingServiceImpl();
	IUserService userService = new UserServiceImpl();
	IReviewService reviewService = new ReviewServiceImpl();
	IUserFollowStoreService userFollowStoreService = new UserFollowStoreServiceImpl();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();
		UserModel userModel = (UserModel) session.getAttribute(Constant.userSession);
		StoreModel storeModel = (StoreModel) session.getAttribute(Constant.storeSession);
		if (storeModel == null || userModel == null) {
			resp.sendRedirect(req.getContextPath() + "/login-customer");
			return;
		}
		req.setAttribute("user", userModel);
		String url = req.getRequestURL().toString();
		if (url.contains("/register-shop")) {
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/createShop.jsp");
			rDispatcher.forward(req, resp);
			return;
		}
		if (url.contains("/vendor/dashboard")) {
			GetDashBoard(req, resp, storeModel);
			return;
		}
		if (url.contains("/vendor/view-shop-info")) {
			req.setAttribute("storeAvatar", "");
			req.setAttribute("storeCover", "");
			req.setAttribute("storeFeatured", "");
			if (storeModel.getAvatar() != null && !storeModel.getAvatar().trim().equals("")) {
				req.setAttribute("storeAvatar", "/AZShop/image?fname=" + storeModel.getAvatar());
			}
			if (storeModel.getCover() != null && !storeModel.getCover().trim().equals("")) {
				req.setAttribute("storeCover", "/AZShop/image?fname=" + storeModel.getCover());
			}
			if (storeModel.getFeaturedImage() != null && !storeModel.getFeaturedImage().trim().equals("")) {
				req.setAttribute("storeFeatured", "/AZShop/image?fname=" + storeModel.getFeaturedImage());
			}
			req.setAttribute("storeInfo", storeModel);
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/shopInfo.jsp");
			req.setAttribute("isView", true);
			rDispatcher.forward(req, resp);
			return;
		}
		if (url.contains("/vendor/update-shop-info")) {
			req.setAttribute("storeAvatar", "");
			req.setAttribute("storeCover", "");
			req.setAttribute("storeFeatured", "");
			if (storeModel.getAvatar() != null && !storeModel.getAvatar().trim().equals("")) {
				req.setAttribute("storeAvatar", "/AZShop/image?fname=" + storeModel.getAvatar());
			}
			if (storeModel.getCover() != null && !storeModel.getCover().trim().equals("")) {
				req.setAttribute("storeCover", "/AZShop/image?fname=" + storeModel.getCover());
			}
			if (storeModel.getFeaturedImage() != null && !storeModel.getFeaturedImage().trim().equals("")) {
				req.setAttribute("storeFeatured", "/AZShop/image?fname=" + storeModel.getFeaturedImage());
			}
			req.setAttribute("storeInfo", storeModel);
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/shopInfo.jsp");
			req.setAttribute("isView", false);
			rDispatcher.forward(req, resp);
			return;
		}
		if (url.contains("/vendor/product/all")) {
			AllProduct(req, resp, storeModel.getId());
			return;
		}
		if (url.contains("/vendor/product/delete")) {
			DeleteProduct(req, resp);
			return;
		}
		if (url.contains("/vendor/product/new") || url.contains("/vendor/product/edit")) {
			doGetProduct(req, resp);
			return;
		}
		if (url.contains("/vendor/order")) {
			if (url.contains("/all")) {
				List<OrderModel> orderModels = orderService.getByStoreId(storeModel.getId());
				getOrderList(req, resp, orderModels);
			} else if (url.contains("/processed")) {
				List<OrderModel> orderModels = orderService.getProcessed(storeModel.getId());
				getOrderList(req, resp, orderModels);
			} else if (url.contains("/cancelled")) {
				List<OrderModel> orderModels = orderService.getCancelled(storeModel.getId());
				getOrderList(req, resp, orderModels);
			} else if (url.contains("/detail")) {
				DetailOrder(req, resp, url, storeModel.getId());
			}
		}
		if (url.contains("vendor/logout")) {
			{
				if (userModel != null)
					session.removeAttribute(Constant.userSession);
				if (storeModel != null)
					session.removeAttribute(Constant.storeSession);
				resp.sendRedirect(req.getContextPath() + "/login-customer");
			}
		}
		if (url.contains("vendor/order/status")) {
			ChangeStatusOrder(req, resp);
		} else if (url.contains("vendor/pickup-address")) {
			GetPickupAddress(req, resp, userModel, storeModel);
		} else if (url.contains("/vendor/review/detail")) {
			GetDetailReview(req, resp, storeModel);
		} else if (url.contains("/vendor/review")) {
			GetReview(req, resp, storeModel);
		} else if (url.contains("vendor/statistics-revenue")) {
			GetRevenue(req, resp, storeModel);
		} else if (url.contains("vendor/statistics-product")) {
			GetStatisticsProduct(req, resp, storeModel);
		}
	}

	private void GetDetailReview(HttpServletRequest req, HttpServletResponse resp, StoreModel storeModel)
			throws ServletException, IOException {
		try {
			String slug = req.getParameter("slug");
			String idReview = req.getParameter("id");
			ProductModel productModel = productService.getBySlug(slug);
			if (productModel == null) {
				req.getRequestDispatcher("/404.jsp").forward(req, resp);
				return;
			}
			List<ReviewModel> reviewModels = reviewService.getByProductId(productModel.getId());
			req.setAttribute("name", productModel.getName());
			req.setAttribute("reviews", reviewModels);
			;
			req.setAttribute("id", idReview);
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/detailReview.jsp");
			rDispatcher.forward(req, resp);

		} catch (Exception e) {
			req.getRequestDispatcher("/404.jsp").forward(req, resp);
		}
		return;

	}

	private void GetDashBoard(HttpServletRequest req, HttpServletResponse resp, StoreModel storeModel)
			throws ServletException, IOException {
		req.setAttribute("store", storeModel); // eWallet, rating, name
		List<ProductModel> productModels = productService.getByStoreId(storeModel.getId()); // totalProducts
		if (productModels != null)
			req.setAttribute("totalProducts", productModels.size());

		BigDecimal totalRevenue = orderService.getSumRevenueByStore(storeModel.getId()); // totalRevenue
		req.setAttribute("totalRevenue", totalRevenue);

		int soldInDay = productService.countInDayByStore(storeModel.getId());
		req.setAttribute("totalSoldInDay", soldInDay);

		BigDecimal yesterdayRevenue = orderService.GetRevenueLastNDays(1, storeModel.getId()).get(0);
		req.setAttribute("revenueYday", yesterdayRevenue);

		int totalReviews = reviewService.countByStore(storeModel.getId());
		req.setAttribute("totalReview", totalReviews);

		int totalCompleted = orderService.countCompletedByStore(storeModel.getId());
		req.setAttribute("totalCompleted", totalCompleted);

		int totalFollows = userFollowStoreService.countByStore(storeModel.getId());
		req.setAttribute("totalFollows", totalFollows);

		int totalOrders = orderService.countOrderByStore(storeModel.getId());
		req.setAttribute("totalOrders", totalOrders);

		int newReviews = reviewService.countNewByStore(storeModel.getId());
		req.setAttribute("newReviews", newReviews);

		RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/dashboard.jsp");
		rDispatcher.forward(req, resp);
	}

	private void GetStatisticsProduct(HttpServletRequest req, HttpServletResponse resp, StoreModel storeModel)
			throws ServletException, IOException {
		ProductModel productModel = productService.getBestSellerProduct(storeModel.getId());
		int totalProduct = productService.countAllByStore(storeModel.getId());
		int totalSales = productService.countSaleByStore(storeModel.getId());
		int totalInDay = productService.countInDayByStore(storeModel.getId());
		List<ProductModel> productModels = productService.getHotProduct(storeModel.getId());
		req.setAttribute("totalProduct", totalProduct);
		req.setAttribute("totalSales", totalSales);
		req.setAttribute("bestSeller", productModel);
		req.setAttribute("totalInday", totalInDay);
		req.setAttribute("products", productModels);
		req.getRequestDispatcher("/views/vendor/statisticsProduct.jsp").forward(req, resp);

	}

	private void GetRevenue(HttpServletRequest req, HttpServletResponse resp, StoreModel storeModel)
			throws ServletException, IOException {
		String nDay = req.getParameter("nDay");
		List<BigDecimal> revenueNDay = null;
		if (nDay != null) {
			int intNDay = Integer.parseInt(nDay);
			revenueNDay = orderService.GetRevenueLastNDays(intNDay, storeModel.getId());
			req.setAttribute("nDay", intNDay);
		} else {
			req.setAttribute("nDay", 10);
			revenueNDay = orderService.GetRevenueLastNDays(10, storeModel.getId());
		}
		req.setAttribute("eWallet", storeModel.geteWallet());
		req.setAttribute("revenues", RevenueData.generateRevenueDataList(revenueNDay));
		req.setAttribute("totalRevenue", orderService.getSumRevenueByStore(storeModel.getId()));
		req.getRequestDispatcher("/views/vendor/statisticsRevenue.jsp").forward(req, resp);

	}

	private void GetReview(HttpServletRequest req, HttpServletResponse resp, StoreModel storeModel)
			throws ServletException, IOException {
		List<ProductModel> productModels = productService.getByStoreId(storeModel.getId());
		List<ReviewModel> reviewModels = reviewService.getByStoreId(storeModel.getId());
		if (reviewModels != null) {
			req.setAttribute("count", reviewModels.size());
		} else {
			req.setAttribute("count", 0);
		}
		req.setAttribute("reviews", reviewModels);
		req.setAttribute("products", productModels);
		req.getRequestDispatcher("/views/vendor/reviews.jsp").forward(req, resp);

	}

	private void GetPickupAddress(HttpServletRequest req, HttpServletResponse resp, UserModel userModel,
			StoreModel storeModel) throws ServletException, IOException {
		String message = req.getParameter("message");
		String error = req.getParameter("error");
		if (message != null) {
			req.setAttribute("message", message);
		}
		if (error != null) {
			req.setAttribute("error", error);
		}

		req.setAttribute("user", userModel);
		req.setAttribute("optionAddress", addressShippingService.getByUserId(userModel.getId()));
		req.getRequestDispatcher("/views/vendor/pickupAddress.jsp").forward(req, resp);

	}

	private void DetailOrder(HttpServletRequest req, HttpServletResponse resp, String url, int storeId)
			throws ServletException, IOException {
		URI uri;
		try {
			uri = new URI(url);
			String path = uri.getPath();
			String[] parts = path.split("/");
			if (parts.length > 0) {
				int id = Integer.parseInt(parts[parts.length - 1]);
				OrderModel orderModel = orderService.getById(id);
				if (orderModel == null) {
					req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
				} else {
					int countItem = orderItemService.countByOrder(orderModel.getId());
					req.setAttribute("countItem", countItem);
					req.setAttribute("order", orderModel);
					req.setAttribute("status", orderService.statusForVendor());
					List<OrderItemModel> orderItemModels = orderItemService.getByOrderId(id);
					req.setAttribute("orderItems", orderItemModels);
					List<ProductModel> productModels = productService.getByStoreId(storeId);

					List<ImageModel> imageModels = new ArrayList<ImageModel>();
					req.setAttribute("products", productModels);
					for (ProductModel productModel : productModels) {
						imageModels.add(imageService.getImage(productModel.getId()));
					}
					req.setAttribute("images", imageModels);
					req.setAttribute("categorys", categoryService.getAll());
					List<DeliveryModel> deliveryModels = deliveryService.getAll();
					req.setAttribute("delivery", deliveryModels);
					req.getRequestDispatcher("/views/vendor/orderDetails.jsp").forward(req, resp);
				}
			}
		} catch (URISyntaxException e) {
			req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
			e.printStackTrace();
		}
		System.out.println("aa check commit");

	}

	private void getOrderList(HttpServletRequest req, HttpServletResponse resp, List<OrderModel> orderModels)
			throws ServletException, IOException {
		String message = req.getParameter("message");
		String error = req.getParameter("error");
		if (message != null) {
			req.setAttribute("message", message);
		}
		if (error != null) {
			req.setAttribute("error", error);
		}
		List<DeliveryModel> deliveryModels = deliveryService.getAll();
		req.setAttribute("status", orderService.statusForVendor());
		req.setAttribute("orders", orderModels);
		req.setAttribute("deliverys", deliveryModels);
		RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/order.jsp");
		rDispatcher.forward(req, resp);

	}

	private void DeleteProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		URI uri;
		try {
			uri = new URI(url);
			String path = uri.getPath();
			String[] parts = path.split("/");
			if (parts.length > 0) {
				String slug = parts[parts.length - 1];
				ProductModel productModel = productService.getBySlug(slug);
				if (productModel == null) {
					req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
				} else {

					try {
						productService.delete(productModel.getId());
						req.setAttribute("message", "The product has been successfully deleted.");
						RequestDispatcher dispatcher = req.getRequestDispatcher("vendor/product/all");
						dispatcher.forward(req, resp);
					} catch (Exception e) {
						req.setAttribute("error", "The deletion of the product was unsuccessful.");
						RequestDispatcher dispatcher = req.getRequestDispatcher("vendor/product/all");
						dispatcher.forward(req, resp);
					}
				}
			}
		} catch (Exception e) {
			req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
		}
	}

	private void AllProduct(HttpServletRequest req, HttpServletResponse resp, int storeId)
			throws ServletException, IOException {
		String categorySlug = req.getParameter("category");
		String saveType = req.getParameter("save");
		String message = req.getParameter("message");
		String error = req.getParameter("error");
		String searchText = req.getParameter("search");
		if (message != null) {
			req.setAttribute("message", message);
		}
		if (error != null) {
			req.setAttribute("error", error);
		}
		if (saveType != null) {
			req.setAttribute("saveType", saveType);
		}
		List<ProductModel> listProductModels = null;
		CategoryModel categoryModel = categoryService.getCategoryBySlug(categorySlug);
		if (categorySlug == null) {
			listProductModels = productService.getBySearch(-1, storeId, saveType, searchText);
		} else if (categoryModel == null) {
			req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
		} else {
			listProductModels = productService.getBySearch(categoryModel.getId(), storeId, saveType, searchText);
		}
		List<CategoryModel> listCategoryModels = categoryService.getAll();
		List<StyleValueModel> listStyleValueModels = styleValueService.getAll();
		req.setAttribute("products", listProductModels);
		req.setAttribute("styleValues", listStyleValueModels);
		req.setAttribute("categorys", listCategoryModels);
		List<ImageModel> lisImageModels = new ArrayList<ImageModel>();
		int sumDraft = productService.countDraftByStore(storeId);
		req.setAttribute("sumDraft", sumDraft);
		int sumPublish = productService.countPublishByStore(storeId);
		req.setAttribute("sumPublish", sumPublish);
		int sumAll = productService.countAllByStore(storeId);
		req.setAttribute("sumAll", sumAll);
		for (ProductModel productModel : listProductModels) {
			lisImageModels.add(imageService.getImage(productModel.getId()));
		}
		req.setAttribute("images", lisImageModels);
		RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/allProduct.jsp");
		rDispatcher.forward(req, resp);

	}

	private void doGetProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = req.getParameter("message");
		String error = req.getParameter("error");
		if (message != null) {
			req.setAttribute("message", message);
		}
		if (error != null) {
			req.setAttribute("error", error);
		}
		String url = req.getRequestURL().toString();
		String categoryIdParam = req.getParameter("categoryId");
		String styleIdParam = req.getParameter("styleId");
		if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
			int categoryId = Integer.parseInt(categoryIdParam);

			List<StyleModel> styles = styleService.getByCategoryId(categoryId);

			// Chuyển danh sách styles thành JSON và gửi về client
			String json = new Gson().toJson(styles);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(json);
		} else if (styleIdParam != null && !styleIdParam.isEmpty()) {
			int styleId = Integer.parseInt(styleIdParam);

			List<StyleValueModel> styleValues = styleValueService.getByStyleId(styleId);

			// Chuyển danh sách styles thành JSON và gửi về client
			String json = new Gson().toJson(styleValues);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(json);
		} else {

			String action = "";
			List<CategoryModel> categoryModels = categoryService.getAll();
			req.setAttribute("categorys", categoryModels);

			if (url.contains("/new")) {
				action = "new";

			}
			if (url.contains("/edit")) {
				action = "edit";
				URI uri;
				try {
					uri = new URI(url);
					String path = uri.getPath();

					String[] parts = path.split("/");

					if (parts.length > 0) {
						String slug = parts[parts.length - 1];
						req.setAttribute("slug", slug);
						action = action + slug;
						try {
							ProductModel productModel = productService.getBySlug(slug);
							if (productModel == null) {
								req.getRequestDispatcher("/views/vendor/404.jsp").forward(req, resp);
							} else {
								req.setAttribute("product", productModel);

								List<ImageModel> imageModels = imageService.getByProductId(productModel.getId());
								for (ImageModel imageModel : imageModels) {
									req.setAttribute("image" + imageModel.getImage().charAt(0), imageModel.getImage());
								}
							}

							StyleValueModel styleValueModel = styleValueService.getById(productModel.getStyleValueId());
							StyleModel styleModel = styleService.getById(styleValueModel.getStyleId());
							req.setAttribute("styleModelId", styleModel.getId());

						} catch (Exception e) {
							req.getRequestDispatcher("/404.jsp").forward(req, resp);
						}
					} else {
						req.getRequestDispatcher("/404.jsp").forward(req, resp);
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			req.setAttribute("action", action);
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/views/vendor/product.jsp");
			rDispatcher.forward(req, resp);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();
		StoreModel storeModel = (StoreModel) session.getAttribute(Constant.storeSession);
		UserModel userModel = (UserModel) session.getAttribute(Constant.userSession);
		if (url.contains("register-shop")) {
			RegisterShop(req, resp);
			return;
		}
		if (url.contains("/vendor/product/new")) {
			NewProduct(req, resp, storeModel.getId());
			return;
		}
		if (url.contains("/vendor/update-shop-info")) {
			UpdateShopInfo(req, resp);
			return;
		}
		if (url.contains("vendor/product/edit")) {
			EditProduct(req, resp, url, storeModel.getId());
		}
		if (url.contains("/vendor/pickup-address")) {
			EditPickupAddress(req, resp, userModel);
		}

	}

	private void EditPickupAddress(HttpServletRequest req, HttpServletResponse resp, UserModel userModel)
			throws ServletException, IOException {
		try {
			UserModel userModel2 = userService.getById(userModel.getId());
			String newAddress = req.getParameter("address");
			String newPhone = req.getParameter("phone");
			userModel2.setAddress(newAddress);
			userModel2.setPhone(newPhone);
			if (CheckValid.isValidPhoneNumber(newPhone)) {
				userService.update(userModel2);
				HttpSession session = req.getSession(false);
				session.setAttribute(Constant.userSession, userModel2);
				resp.sendRedirect("?message=The pickup address has been updated.");
			} else {
				resp.sendRedirect("?error=The update has been failed: Invalid phone number.");
			}
		} catch (Exception e) {
			resp.sendRedirect("?error=The update has been failed.");
		}

	}

	private void ChangeStatusOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("id"));
			String status = req.getParameter("status");
			orderService.changeStatus(id, status);
			resp.sendRedirect(req.getContextPath() + "/vendor/order/all?message=The status has been updated.");

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(req.getContextPath() + "/vendor/order/all?error=The update has failed.");
		}

	}

	private void EditProduct(HttpServletRequest req, HttpServletResponse resp, String url, int storeId)
			throws ServletException, IOException {
		try {

			String name = req.getParameter("name");
			String description = req.getParameter("description");
			BigDecimal price = new BigDecimal(req.getParameter("price"));
			int quantity = Integer.parseInt(req.getParameter("quantity"));
			Boolean isActive = true;
			if (req.getParameter("isActive").equals("0")) {
				isActive = false;
			}
			int categoryId = Integer.parseInt(req.getParameter("categoryId"));
			int styleValueId = Integer.parseInt(req.getParameter("styleValueId"));

			URI uri;
			uri = new URI(url);
			String path = uri.getPath();

			String[] parts = path.split("/");

			String slug = parts[parts.length - 1];
			slug = slug.substring(4);

			ProductModel productModel = productService.getBySlug(slug);
			productModel.setName(name);
			productModel.setDescription(description);
			productModel.setPrice(price);
			productModel.setQuantity(quantity);
			productModel.setActive(isActive);
			productModel.setStoreId(storeId);
			String video = "";
			if (req.getPart("video").getSize() != 0) {
				String fileName = "" + System.currentTimeMillis();
				video = UploadUtils.processUpload("video", req, Constant.DIR, fileName);
			}

			if (video != null && video != "") {
				ImageUtil.deleteImage(productModel.getVideo());
				productModel.setVideo(video);
			}
			productModel.setCategoryId(categoryId);
			productModel.setStyleValueId(styleValueId);
			productService.update(productModel);

			String imageName = "image";

			for (int i = 1; i <= 6; i++) {
				String thumbnail = req.getParameter("deletethumbnail" + String.valueOf(i));
				System.out.println(thumbnail);
				if (thumbnail.equals("1")) {
					imageService.deletedByIndex(i, productModel.getId());
				}
			}

			for (int i = 1; i <= 6; i++) {

				imageName += String.valueOf(i);

				if (req.getPart(imageName).getSize() != 0) {
					ImageModel imageModel = new ImageModel();
					String fileName = String.valueOf(i);

					fileName += System.currentTimeMillis();
					imageModel.setImage(UploadUtils.processUpload(imageName, req, Constant.DIR, fileName));
					imageModel.setProductId(productModel.getId());
					imageService.deletedByIndex(i, productModel.getId());
					imageService.insert(imageModel);
				}
				imageName = "image";

			}
			resp.sendRedirect(req.getContextPath() + "/vendor/product/all?message=The product has been updated.");

		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(req.getContextPath() + "/vendor/product/all?error=The update has failed.");
		}
	}

	private void UpdateShopInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		if (!doAction(req, resp)) {
			RequestDispatcher rDispatcher = req.getRequestDispatcher("/404.jsp");
			rDispatcher.forward(req, resp);
			return;
		}

		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		String shopName = req.getParameter("shopName").toString();
		String shopBio = req.getParameter("shopBio").toString();
		String storageLocation = Constant.DIR;
		String coverImage = UploadImage.UploadImageToLocal("coverImage", req, storageLocation);
		String avatarImage = UploadImage.UploadImageToLocal("avatarImage", req, storageLocation);
		String featuredImage = UploadImage.UploadImageToLocal("featuredImage", req, storageLocation);

		HttpSession session = req.getSession();
		StoreModel storeModel = (StoreModel) session.getAttribute(Constant.storeSession);

		if (!shopName.trim().equals("")) {
			storeModel.setName(shopName);
		}
		if (!shopBio.trim().equals("")) {
			storeModel.setBio(shopBio);
		}
		if (!coverImage.trim().equals("")) {
			storeModel.setCover(coverImage);
		}
		if (!avatarImage.trim().equals("")) {
			storeModel.setAvatar(avatarImage);
		}
		if (!featuredImage.trim().equals("")) {
			storeModel.setFeaturedImage(featuredImage);
		}

		String message = "";
		IStoreService storeService = new StoreServiceImpl();
		JsonObject responseStatus = new JsonObject();
		try {
			storeService.update(storeModel);
			message = "Change success!!!";
			System.out.println(message);
			responseStatus.addProperty("status", "Success");
			responseStatus.addProperty("message", message);
			session.setAttribute(Constant.storeSession, storeModel);
		} catch (Exception e) {
			e.printStackTrace();
			message = "Change failse!!!";
			responseStatus.addProperty("status", "Failse");
			responseStatus.addProperty("message", message);
		}
		String jsonResponse = new Gson().toJson(responseStatus);
		resp.getWriter().write(jsonResponse);
	}

	private void RegisterShop(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		// Chuyen Dashboard
		resp.sendRedirect("vendor-dashboard");

	}

	private void NewProduct(HttpServletRequest req, HttpServletResponse resp, int storeId)
			throws ServletException, IOException {
		try {

			String name = req.getParameter("name");
			String slug = SlugUtil.toSlug(name);
			String description = req.getParameter("description");
			BigDecimal price = new BigDecimal(req.getParameter("price"));
			int quantity = Integer.parseInt(req.getParameter("quantity"));
			Boolean isActive = true;
			if (req.getParameter("isActive").equals("0")) {
				isActive = false;
			}
			String video = req.getParameter("video");
			int categoryId = Integer.parseInt(req.getParameter("categoryId"));
			int styleValueId = Integer.parseInt(req.getParameter("styleValueId"));

			ProductModel productModel = new ProductModel(name, slug, description, price, quantity, isActive, categoryId,
					styleValueId, storeId, video);
			if (req.getPart("video").getSize() != 0) {
				String fileName = "" + System.currentTimeMillis();
				productModel.setVideo(UploadUtils.processUpload("video", req, Constant.DIR, fileName));
			}

			productService.insert(productModel);

			productModel = productService.getBySlug(slug);
			String imageName = "image";
			for (int i = 1; i <= 6; i++) {

				imageName += String.valueOf(i);

				if (req.getPart(imageName).getSize() != 0) {
					ImageModel imageModel = new ImageModel();
					String fileName = String.valueOf(i);

					fileName += System.currentTimeMillis();
					imageModel.setImage(UploadUtils.processUpload(imageName, req, Constant.DIR, fileName));
					imageModel.setProductId(productModel.getId());

					imageService.insert(imageModel);
				}
				imageName = "image";

			}
			resp.sendRedirect("?message=The product has been successfully added.");
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect("?error=The addition of the product has failed.");
		}
	}

	public boolean doAction(HttpServletRequest request, HttpServletResponse response) {
		String csrfCookie = null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("csrf")) {
				csrfCookie = cookie.getValue();
			}
		}

		String csrfField = request.getParameter("csrfToken");

		if (csrfCookie == null || csrfField == null || !csrfCookie.equals(csrfField)) {
			return false;
		} else {
			return true;
		}

	}

}
