package com.azshop.controllers.customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.metadata.IIOMetadataFormat;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.azshop.models.AddressShippingModel;
import com.azshop.models.CartItemModel;
import com.azshop.models.CartModel;
import com.azshop.models.CategoryModel;
import com.azshop.models.DeliveryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.OrderItemModel;
import com.azshop.models.OrderModel;
import com.azshop.models.ProductModel;
import com.azshop.models.StoreModel;
import com.azshop.models.UserModel;
import com.azshop.services.AddressShippingServiceImpl;
import com.azshop.services.CartItemServiceImpl;
import com.azshop.services.CartServiceImpl;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.DeliveryServiceImpl;
import com.azshop.services.IAddressShippingService;
import com.azshop.services.ICartItemService;
import com.azshop.services.ICartService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IDeliveryService;
import com.azshop.services.IImageService;
import com.azshop.services.IOrderItemService;
import com.azshop.services.IOrderService;
import com.azshop.services.IProductService;
import com.azshop.services.IStoreService;
import com.azshop.services.IStyleService;
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.OrderItemServiceImpl;
import com.azshop.services.OrderServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;

@WebServlet(urlPatterns = { "/customer/add-to-cart/*", "/guest/add-to-cart/*", "/customer/delete-item-cart",
		"/customer/cart/checkout", "/customer/cart/checkout-comfirm", "/customer/add-address" })
public class CartController extends HttpServlet {

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
	IDeliveryService deliveryService = new DeliveryServiceImpl();
	IAddressShippingService addressShippingService = new AddressShippingServiceImpl();
	IOrderService orderService = new OrderServiceImpl();
	IOrderItemService orderItemService = new OrderItemServiceImpl();
	UserModel user = new UserModel();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();		

		// Kiểm tra nếu URL chứa "guest"
	    if (url.contains("guest")) {
	        // Lấy giá trị của tham số "count" từ URL
	        String countParam = req.getParameter("count");
	        
	        // Kiểm tra nếu tham số "count" không tồn tại hoặc không phải là số 1
	        if (countParam == null || !countParam.equals("1")) {
	            // Chuyển hướng người dùng đến trang error
	        	req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
	            return; // Kết thúc phương thức để ngăn chặn việc tiếp tục xử lý
	        }
	        
	        // Nếu tham số "count" là số 1, chuyển hướng người dùng đến trang login-customer
	        resp.sendRedirect(req.getContextPath() + "/login-customer");
	    }

		try {
			HttpSession session = req.getSession();
			if (session != null) {
				Object sessionObject = session.getAttribute(Constant.userSession);
				if (sessionObject instanceof UserModel) {
					user = (UserModel) sessionObject;
					List<CartModel> cartList = cartService.getByUserId(user.getId());
					List<CartItemModel> cartItemList = new ArrayList<CartItemModel>();

					// Hiển thị item trong giỏ hàng
					for (CartModel cart : cartList) {
						List<CartItemModel> itemList = cartItemService.getByCartId(cart.getId());
						cartItemList.addAll(itemList);
					}

					// Lấy thông tin danh sách product có trong giỏ hàng
					List<ProductModel> productsInCart = new ArrayList<ProductModel>();

					for (CartItemModel cartItem : cartItemList) {
						ProductModel productInCart = productService.getById(cartItem.getProductId());
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

					List<DeliveryModel> deliveryList = deliveryService.getAll();

					req.setAttribute("deliveryList", deliveryList);
					req.setAttribute("quantity", cartItemList.size());
					req.setAttribute("user", user);
					req.setAttribute("imageProductsInCart", imageProductsInCart);
					req.setAttribute("cartItemList", cartItemList);
//					req.setAttribute("productsInCart", productsInCart);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Hiển thị menu danh mục
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);

		List<CartModel> cartList = cartService.getByUserId(user.getId());
		List<CartItemModel> cartItemList = new ArrayList<CartItemModel>();

		// Hiển thị item trong giỏ hàng
		for (CartModel cart : cartList) {
			StoreModel store = storeService.getById(cart.getStoreId());
			cart.setNameStore(store.getName());
			List<CartItemModel> itemList = cartItemService.getByCartId(cart.getId());
			cartItemList.addAll(itemList);
		}
		// Lấy thông tin danh sách product có trong giỏ hàng
		List<ProductModel> productsInCart = new ArrayList<ProductModel>();

		for (CartItemModel cartItem : cartItemList) {
			ProductModel productInCart = productService.getById(cartItem.getProductId());
			productsInCart.add(productInCart);
		}

		List<ImageModel> imageProductsInCart = new ArrayList<ImageModel>();

		for (ProductModel productModel : productsInCart) {
			ImageModel image = imageService.getImage(productModel.getId());
			imageProductsInCart.add(image);
		}

		req.setAttribute("imageProductsInCart", imageProductsInCart);
		req.setAttribute("cartList", cartList);
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("productsInCart", productsInCart);

		if (url.contains("customer/add-to-cart")) {
			try {
				addProductToCart(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (url.contains("customer/cart/checkout")) {
			try {
				List<AddressShippingModel> listAddress = addressShippingService.getByUserId(user.getId());
				req.setAttribute("listAddress", listAddress);
				req.setAttribute("cartItemList", cartItemList);

				RequestDispatcher rd = req.getRequestDispatcher("/views/customer/checkout.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (url.contains("customer/cart/checkout-comfirm")) {
			try {
				req.setAttribute("cartItemList", cartItemList);

				RequestDispatcher rd = req.getRequestDispatcher("/views/customer/checkout.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (url.contains("customer/delete-item-cart")) {
			try {
				deleteCartItem(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (url.contains("customer/add-address")) {
			try {
				RequestDispatcher rd = req.getRequestDispatcher("/views/customer/addAddress.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteCartItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		int cartItemId = Integer.parseInt(req.getParameter("id"));

		cartItemService.delete(cartItemId);

		// chuyển tới trang trước đó
		resp.sendRedirect(req.getHeader("Referer"));
	}

	private void addProductToCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();

		UserModel user = (UserModel) session.getAttribute(Constant.userSession);
		req.setAttribute("user", user);
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/login-customer");
			return;
		}

		String url = req.getRequestURL().toString();
		URI uri;
		try {

			uri = new URI(url);
			String path = uri.getPath();

			String[] parts = path.split("/");

			if (parts.length > 0) {
				String slug = parts[parts.length - 1];
				try {

					ProductModel product = productService.getBySlug(slug);

					// Lấy thử danh sách cart
					List<CartModel> cartList = cartService.getByUserId(user.getId());

					// nếu chưa có cart nào
					if (cartList.size() == 0) {
						// tạo một cart mới
						CartModel cart = new CartModel();
						CartModel newCart = new CartModel();
						newCart.setUserId(user.getId());
						newCart.setStoreId(product.getStoreId());
						cartService.insert(newCart);

						// Lấy lại danh sách cart sau khi vừa thêm mới
						cartList = cartService.getByUserId(user.getId());
						for (CartModel cartModel : cartList) {
							if (product.getStoreId() == cartModel.getStoreId()) {
								cart = cartModel;
							}
						}

						// Thêm item mới vào cart
						CartItemModel newItem = new CartItemModel();
						newItem.setCartId(cart.getId());
						newItem.setProductId(product.getId());
						newItem.setStyleValueId(product.getStyleValueId());
						newItem.setCount(Integer.parseInt(req.getParameter("count")));
						cartItemService.insert(newItem);
					}

					// nếu đã có cart
					else {
						boolean isExistCart = false;
						CartModel cart = new CartModel();

						for (CartModel cartModel : cartList) {
							// Kiểm tra xem store id của product được thêm vào có trong cart nào chưa
							if (product.getStoreId() == cartModel.getStoreId()) {
								isExistCart = true;
								cart = cartModel;
							}
						}

						// nếu đã có thì sẽ tiếp tục thêm sản phẩm vào
						if (isExistCart == true) {
							// Kiểm tra có item trong giỏ hàng chưa
							boolean isExistItemCart = false;
							List<CartItemModel> cartItems = new ArrayList<CartItemModel>();
							for (CartModel cartModel : cartList) {
								cartItems.addAll(cartItemService.getByCartId(cartModel.getId()));
							}

							CartItemModel itemInCart = new CartItemModel();
							for (CartItemModel item : cartItems) {
								if (product.getId() == item.getProductId()) {
									isExistItemCart = true;
									itemInCart = item;
								}
							}
							// Nếu chưa có item trong cart
							if (isExistItemCart == false) {
								// Thêm item mới vào cart
								CartItemModel newItem = new CartItemModel();
								newItem.setCartId(cart.getId());
								newItem.setProductId(product.getId());
								newItem.setStyleValueId(product.getStyleValueId());
								newItem.setCount(Integer.parseInt(req.getParameter("count")));
								cartItemService.insert(newItem);
							}
							// Nếu đã có thì tăng thêm số lượng
							else {
								int count = Integer.parseInt(req.getParameter("count"));

								itemInCart.setCount(itemInCart.getCount() + count);
								cartItemService.update(itemInCart);
							}
						}

						// nếu chưa có thì tạo cart mới cho store id này
						else {
							CartModel newCart = new CartModel();
							newCart.setUserId(user.getId());
							newCart.setStoreId(product.getStoreId());
							cartService.insert(newCart);

							// Lấy lại danh sách cart sau khi vừa thêm mới
							cartList = cartService.getByUserId(user.getId());
							for (CartModel cartModel : cartList) {
								if (product.getStoreId() == cartModel.getStoreId()) {
									cart = cartModel;
								}
							}

							// Thêm item cho cart
							CartItemModel cartItem = new CartItemModel();
							cartItem.setCartId(cart.getId());
							cartItem.setProductId(product.getId());
							cartItem.setStyleValueId(product.getStyleValueId());
							cartItem.setCount(Integer.parseInt(req.getParameter("count")));
							cartItemService.insert(cartItem);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// chuyển tới trang trước đó
		resp.sendRedirect(req.getHeader("Referer"));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		if (url.contains("/customer/cart/checkout-comfirm")) {
			BufferedReader reader = req.getReader();
			StringBuilder requestBody = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				requestBody.append(line);
			}
			String jsonString = requestBody.toString();

			String otherAddressString = "0";
			String shippingValueString = "0";
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

				otherAddressString = (String) jsonObject.get("otherAddress");
				shippingValueString = (String) jsonObject.get("shippingValue");

			} catch (ParseException e) {
				e.printStackTrace();
			}
			int otherAddress = Integer.parseInt(otherAddressString);
			int shippingValue = Integer.parseInt(shippingValueString);

			if (shippingValue == 0) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("status", "Error");
				responseJson.put("message", "Shipping value is 0");
				String jsonResponse = responseJson.toJSONString();
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.getWriter().write(jsonResponse);
			} else {
				List<CartModel> cartList = cartService.getByUserId(user.getId());

				for (CartModel cart : cartList) {

					OrderModel orderModel = new OrderModel();

					orderModel.setUserId(user.getId());
					orderModel.setStoreId(cart.getStoreId());
					orderModel.setDeliveryId(shippingValue);

					long currentTimeInSeconds = Instant.now().getEpochSecond();
					String currentTimeString = String.valueOf(user.getId()) + String.valueOf(currentTimeInSeconds);
					orderModel.setStatus(currentTimeString);

					if (otherAddress == 0) {
						orderModel.setRecipientName(user.getFirstName() + user.getLastName());
						orderModel.setAddress(user.getAddress());
						orderModel.setPhone(user.getPhone());
					} else {
						AddressShippingModel addressShipping = addressShippingService.getById(otherAddress);
						orderModel.setRecipientName(addressShipping.getRecipientName());
						orderModel.setAddress(addressShipping.getAddress());
						orderModel.setPhone(addressShipping.getPhone());
					}

					orderModel.setPaidBefore(false);
					orderModel.setAmountFromUser(BigDecimal.valueOf(0));
					orderModel.setAmountFromStore(BigDecimal.valueOf(0));
					orderModel.setAmountToStore(BigDecimal.valueOf(0));
					orderModel.setAmountToAZShop(BigDecimal.valueOf(0));

					orderService.insert(orderModel);

					OrderModel newOrderModel = orderService.getByStatus(currentTimeString);

					newOrderModel.setStatus("Waiting");

					List<CartItemModel> cartItemList = cartItemService.getByCartId(cart.getId());

					DeliveryModel deliveryModel = deliveryService.getById(shippingValue);
					double totalPrice = deliveryModel.getPrice().doubleValue();

					for (CartItemModel cartItem : cartItemList) {
						ProductModel product = productService.getById(cartItem.getProductId());
						product.setQuantity(product.getQuantity() - 1);
						product.setSold(product.getSold() + 1);
						OrderItemModel orderItem = new OrderItemModel();

						totalPrice = totalPrice + product.getPrice().doubleValue() * cartItem.getCount();

						orderItem.setOrderId(newOrderModel.getId());
						orderItem.setProductId(product.getId());
						orderItem.setCount(cartItem.getCount());

						orderItemService.insert(orderItem);
						productService.update(product);
						cartItemService.delete(cartItem.getId());
					}

					newOrderModel.setAmountFromUser(BigDecimal.valueOf(totalPrice));
					newOrderModel.setAmountFromStore(BigDecimal.valueOf(totalPrice * 0.07));
					newOrderModel.setAmountToStore(BigDecimal.valueOf(totalPrice - totalPrice * 0.07));
					newOrderModel.setAmountToAZShop(BigDecimal.valueOf(totalPrice * 0.07));

					orderService.update(newOrderModel);
					cartService.delete(cart.getId());
				}

				System.out.println("Success");

				JSONObject responseJson = new JSONObject();
				responseJson.put("status", "Success");
				responseJson.put("message", "Success");
				String jsonResponse = responseJson.toJSONString();
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.getWriter().write(jsonResponse);
			}
			return;
		}
		if (url.contains("/customer/add-address")) {
			req.setCharacterEncoding("UTF-8");
			resp.setCharacterEncoding("UTF-8");

			String recipientName = req.getParameter("recipientName");
			String address = req.getParameter("address");
			String phone = req.getParameter("phone");

			AddressShippingModel addressShipping = new AddressShippingModel();
			addressShipping.setUserId(user.getId());
			addressShipping.setRecipientName(recipientName);
			addressShipping.setAddress(address);
			addressShipping.setPhone(phone);

			addressShippingService.insert(addressShipping);

			resp.sendRedirect("cart/checkout");
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
