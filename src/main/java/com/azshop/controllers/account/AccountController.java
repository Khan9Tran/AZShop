package com.azshop.controllers.account;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.azshop.models.CartModel;
import com.azshop.models.OrderItemModel;
import com.azshop.models.OrderModel;
import com.azshop.models.ProductModel;
import com.azshop.models.StoreModel;
import com.azshop.models.UserModel;
import com.azshop.services.IOrderItemService;
import com.azshop.services.IOrderService;
import com.azshop.services.IProductService;
import com.azshop.services.IStoreService;
import com.azshop.services.IUserService;
import com.azshop.services.OrderItemServiceImpl;
import com.azshop.services.OrderServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;
import com.azshop.utils.Email;
import com.azshop.utils.UploadUtils;

@WebServlet(urlPatterns = { "/login-customer", "/verify-customer", "/register-customer", "/forget-customer",
		"/logout-customer", "/reset-success-customer", "/information", "/update-infor", "/update-password", "/waiting",
		"/detailOrder", "/cancellOrder" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024
		* 50)

public class AccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	IUserService userService = new UserServiceImpl();
	IOrderService orderService = new OrderServiceImpl();
	IOrderItemService orderItemService = new OrderItemServiceImpl();
	IProductService productService = new ProductServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("X-Content-Type-Options", "nosniff");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String url = req.getRequestURI().toString();

		if (url.contains("register-customer")) {
			getRegister(req, resp);
		} else if (url.contains("verify-customer")) {
			getVerify(req, resp);
		} else if (url.contains("login-customer")) {
			getLogin(req, resp);
		} else if (url.contains("forget-customer")) {
			getForget(req, resp);
		} else if (url.contains("logout-customer")) {
			getLogout(req, resp);
		} else if (url.contains("reset-success-customer")) {
			getResetSuccess(req, resp);
		} else if (url.contains("information")) {
			getInfor(req, resp);
		} else if (url.contains("waiting")) {
			getWaiting(req, resp);
		} else if (url.contains("detailOrder")) {
			getOrderInformation(req, resp);
		}
	}

	private void getOrderInformation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Kiểm tra xem có phiên đăng nhập (session) và có thông tin người dùng không
		HttpSession session = req.getSession();
		if (session != null && session.getAttribute(Constant.userSession) != null) {
			// Lấy thông tin người dùng từ session
			UserModel user = (UserModel) session.getAttribute(Constant.userSession);

			// Lấy orderId từ tham số truyền vào
			Integer orderId = Integer.parseInt(req.getParameter("id"));

			// Khởi tạo danh sách chứa thông tin sản phẩm và đặt hàng
			List<ProductModel> product = new ArrayList<>();
			List<OrderItemModel> orderItem = orderItemService.getByOrderId(orderId);

			// Lấy thông tin sản phẩm tương ứng với từng đơn hàng
			for (OrderItemModel orderItems : orderItem) {
				product.add(productService.getById(orderItems.getProductId()));
			}

			// Đặt các thông tin cần thiết vào request để chuyển đến trang hiển thị thông
			// tin đơn hàng
			req.setAttribute("product", product);
			req.setAttribute("orderItems", orderItem);
			req.setAttribute("size", orderItem.size());
			req.setAttribute("user", user);

			// Chuyển hướng đến trang hiển thị thông tin đơn hàng
			req.getRequestDispatcher("/views/account/order-information.jsp").forward(req, resp);
		}
	}

	private void getWaiting(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy phiên (session) hiện tại từ request
		HttpSession session = req.getSession();

		// Kiểm tra xem phiên có tồn tại và có thông tin người dùng không
		if (session != null && session.getAttribute(Constant.userSession) != null) {
			// Nếu có, lấy thông tin người dùng từ session
			UserModel user = (UserModel) session.getAttribute(Constant.userSession);

			// Kiểm tra vai trò của người dùng
			if ("customer".equals(user.getRole())) {
				// Nếu vai trò là "customer", chuyển hướng đến trang chủ của khách hàng
				resp.sendRedirect(req.getContextPath() + "/customer-home");
			} else if ("vendor".equals(user.getRole())) {
				// Nếu vai trò là "vendor", lấy thông tin cửa hàng và chuyển hướng đến trang
				// dashboard của người bán
				IStoreService storeService = new StoreServiceImpl();
				StoreModel storeModel = storeService.getByOwnerId(user.getId());
				session.setAttribute(Constant.storeSession, storeModel);
				resp.sendRedirect(req.getContextPath() + "/vendor/dashboard");
			} else if ("admin".equals(user.getRole())) {
				// Nếu vai trò là "admin", chuyển hướng đến trang dashboard của admin
				resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
			}
		} else {
			// Nếu không có phiên đăng nhập, chuyển hướng đến trang đăng nhập của khách hàng
			resp.sendRedirect(req.getContextPath() + "/login-customer");
		}
	}

	private void getInfor(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy thông báo thành công từ tham số request (nếu có) và đặt vào thuộc tính
		// "done"
		String done = req.getParameter("done");
		req.setAttribute("done", done);

		// Lấy phiên (session) hiện tại từ request
		HttpSession session = req.getSession();

		// Khởi tạo đối tượng UserModel để chứa thông tin người dùng
		UserModel user = new UserModel();

		// Kiểm tra xem phiên có tồn tại không
		if (session != null) {
			// Nếu có, lấy thông tin người dùng từ session
			user = (UserModel) session.getAttribute(Constant.userSession);

			// Đặt thông tin người dùng vào thuộc tính "user"
			req.setAttribute("user", user);
		}

		// Lấy danh sách đơn hàng theo trạng thái và đặt vào các thuộc tính tương ứng
		List<OrderModel> orderListWaiting = orderService.getByUserIdandStatus(user.getId(), "Waiting");
		List<OrderModel> orderListProcessing = orderService.getByUserIdandStatus(user.getId(), "Processing");
		List<OrderModel> orderListPending = orderService.getByUserIdandStatus(user.getId(), "Pending Pickup");
		List<OrderModel> orderListShipping = orderService.getByUserIdandStatus(user.getId(), "Shipping");
		List<OrderModel> orderListDelivered = orderService.getByUserIdandStatus(user.getId(), "Delivered");
		List<OrderModel> orderListCompleted = orderService.getByUserIdandStatus(user.getId(), "Completed");
		List<OrderModel> orderListCancelled = orderService.getByUserIdandStatus(user.getId(), "Cancelled");

		// Đặt danh sách đơn hàng vào các thuộc tính để chuyển đến trang hiển thị
		req.setAttribute("orderListWaiting", orderListWaiting);
		req.setAttribute("orderListProcessing", orderListProcessing);
		req.setAttribute("orderListPending", orderListPending);
		req.setAttribute("orderListShipping", orderListShipping);
		req.setAttribute("orderListDelivered", orderListDelivered);
		req.setAttribute("orderListCompleted", orderListCompleted);
		req.setAttribute("orderListCancelled", orderListCancelled);

		// Chuyển hướng đến trang hiển thị thông tin người dùng
		req.getRequestDispatcher("/views/account/information.jsp").forward(req, resp);
	}

	private void getResetSuccess(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		req.getRequestDispatcher("/views/account/reset-success.jsp").forward(req, resp);
	}

	private void getLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Xóa các sesion còn tồn tại
		HttpSession session = req.getSession();
		session.removeAttribute(Constant.userSession);
		session.removeAttribute(Constant.storeSession);

		resp.sendRedirect("./guest-home");
	}

	private void getForget(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		req.getRequestDispatcher("/views/account/forget.jsp").forward(req, resp);
	}

	private void getLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Kiểm tra còn session thì không cần đăng nhập lại
		HttpSession session = req.getSession(false);
		if (session != null && session.getAttribute(Constant.userSession) != null) {
			resp.sendRedirect(req.getContextPath() + "/waiting");
			return;
		}
		req.getRequestDispatcher("/views/account/login.jsp").forward(req, resp);
	}

	private void getVerify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		req.getRequestDispatcher("/views/account/verify.jsp").forward(req, resp);
	}

	private void getRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		req.getRequestDispatcher("/views/account/register.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		resp.setHeader("X-Frame-Options", "DENY");
		resp.setHeader("X-Content-Type-Options", "nosniff");

		if (url.contains("register-customer")) {
			if (!doAction(req, resp)) {
				RequestDispatcher rDispatcher = req.getRequestDispatcher("/404.jsp");
				rDispatcher.forward(req, resp);
				return;
			}
			postRegister(req, resp);
		} else if (url.contains("verify-customer")) {
			if (!doAction(req, resp)) {
				RequestDispatcher rDispatcher = req.getRequestDispatcher("/404.jsp");
				rDispatcher.forward(req, resp);
				return;
			}
			postVerify(req, resp);
		} else if (url.contains("login-customer")) {
			if (!doAction(req, resp)) {
				RequestDispatcher rDispatcher = req.getRequestDispatcher("/404.jsp");
				rDispatcher.forward(req, resp);
				return;
			}
			postLogin(req, resp);
		} else if (url.contains("forget-customer")) {
			if (!doAction(req, resp)) {
				RequestDispatcher rDispatcher = req.getRequestDispatcher("/404.jsp");
				rDispatcher.forward(req, resp);
				return;
			}
			postForget(req, resp);
		} else if (url.contains("update-infor")) {
			postUpdateInfor(req, resp);
		} else if (url.contains("update-password")) {
			postUpdatePassword(req, resp);
		} else if (url.contains("cancellOrder")) {
			postCancell(req, resp);
		}
	}

	private void postCancell(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Đặt trạng thái bằng cancelled
		try {
			Integer orderId = Integer.parseInt(req.getParameter("id"));
			orderService.changeStatus(orderId, "Cancelled");
			resp.sendRedirect("/AZShop/information?done= Don hang duoc xoa thanh cong!");
		} catch (Exception e) {
			resp.sendRedirect("/AZShop/information?done= Don hang xoa khong thanh cong!");
		}

	}

	private void postUpdatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Khởi tạo đối tượng UserModel để chứa thông tin người dùng
		UserModel user = new UserModel();

		// Kiểm tra xem có phiên đăng nhập (session) và có thông tin người dùng không
		HttpSession session = req.getSession();
		if (session != null && session.getAttribute(Constant.userSession) != null) {
			// Nếu có, lấy thông tin người dùng từ session và gán vào đối tượng user
			user = (UserModel) session.getAttribute(Constant.userSession);
		}

		// Lấy thông tin mật khẩu từ form
		String currentPass = req.getParameter("currentPassword");
		String newPassword = req.getParameter("newPassword");
		String renewPassword = req.getParameter("renewPassword");

		// Tạo chuỗi mật khẩu đã hash để so sánh với mật khẩu hiện tại của người dùng
		String hashedPassword = currentPass + "-" + user.getSalt();

		// Kiểm tra xem mật khẩu hiện tại nhập vào có khớp với mật khẩu của người dùng
		// không
		// và kiểm tra xem mật khẩu mới và nhập lại mật khẩu mới có trùng khớp không
		if (hashedPassword.equals(user.getHashedPassword()) && newPassword.equals(renewPassword)) {
			// Nếu mọi thứ đều hợp lệ, cập nhật mật khẩu mới cho người dùng
			userService.updatePassword(user, newPassword);

			// Chuyển hướng đến trang chủ với thông báo thành công
			resp.sendRedirect(req.getContextPath() + "/guest-home");
		} else {
			// Nếu có lỗi, chuyển hướng đến trang thông tin người dùng với thông báo lỗi
			resp.sendRedirect("/AZShop/information?done=Mat khau duoc doi khong thanh cong!");
		}
	}

	private void postUpdateInfor(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Khởi tạo đối tượng UserModel để chứa thông tin người dùng
		UserModel user = new UserModel();

		// Kiểm tra xem có phiên đăng nhập (session) và có thông tin người dùng không
		HttpSession session = req.getSession();
		if (session != null && session.getAttribute(Constant.userSession) != null) {
			// Nếu có, lấy thông tin người dùng từ session và gán vào đối tượng user
			user = (UserModel) session.getAttribute(Constant.userSession);
		}

		// Xử lý upload ảnh nếu có
		String image = null;
		if (req.getPart("Image").getSize() != 0) {
			// Tạo tên file mới bằng thời điểm hiện tại để tránh trùng lặp
			String fileName = "" + System.currentTimeMillis();
			// Xử lý upload và nhận đường dẫn của ảnh
			image = UploadUtils.processUpload("Image", req, Constant.DIR, fileName);
		}

		// Cập nhật thông tin người dùng với các giá trị từ form
		user.setFirstName(req.getParameter("firstName"));
		user.setLastName(req.getParameter("lastName"));
		user.setEmail(user.getEmail()); // Lưu ý: đoạn này có thể cần được chỉnh sửa nếu bạn muốn cập nhật email từ form
		user.setPhone(req.getParameter("phone"));
		user.setAddress(req.getParameter("address"));
		user.setAvatar(image);
		
		if (req.getParameter("phone").length() < 10) {
			resp.sendRedirect("/AZShop/information?done=Cap nhat thong tin khong thanh cong!");
			return;
		} else {
			// Gọi service để cập nhật thông tin người dùng
			userService.update(user);

			// Chuyển hướng đến trang thông tin người dùng với thông báo thành công
			resp.sendRedirect("/AZShop/information?done=Cap nhat thong tin thanh cong!");
		}
	}

	private void postForget(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy giá trị email từ tham số request
		String email = req.getParameter("username");

		// Kiểm tra xem email có tồn tại trong hệ thống không
		if (userService.checkExistEmial(email)) {
			// Nếu email tồn tại, tạo đối tượng Email để gửi mã xác nhận
			Email mail = new Email();

			// Tạo mật khẩu mới ngẫu nhiên
			String newPassword = mail.getRandom();

			// Lấy thông tin người dùng từ email
			UserModel user = userService.getByEmail(email);

			// Gửi email với mật khẩu mới và kiểm tra xem việc gửi email có thành công không
			boolean test = mail.sendEmailForget(user, newPassword);

			if (test) {
				// Nếu gửi email thành công, cập nhật mật khẩu mới cho người dùng
				userService.updatePassword(user, newPassword);

				// Chuyển hướng đến trang thông báo thành công
				resp.sendRedirect(req.getContextPath() + "/reset-success-customer");
			} else {
				// Nếu gửi email thất bại, đặt thuộc tính lỗi và chuyển hướng đến trang quên mật
				// khẩu
				req.setAttribute("forGetError", "Lỗi khi gửi mail");
				req.getRequestDispatcher("/views/account/forget.jsp").forward(req, resp);
			}
		} else {
			// Nếu email không tồn tại, đặt thuộc tính lỗi và chuyển hướng đến trang quên
			// mật khẩu
			req.setAttribute("forGetError", "Email không tồn tại!!!");
			req.getRequestDispatcher("/views/account/forget.jsp").forward(req, resp);
		}
	}

	private void postLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		
		try {
		    // Lấy giá trị từ tham số request
		    String username = req.getParameter("username");
		    String password = req.getParameter("password");

		    // Kiểm tra xem tên đăng nhập và mật khẩu có được nhập không
		    if (username.isEmpty() || password.isEmpty()) {
		        // Nếu không nhập đầy đủ thông tin, chuyển hướng đến trang đăng nhập để nhập lại
		        req.getRequestDispatcher("/views/account/login.jsp").forward(req, resp);
		        return;
		    }

		    // Thực hiện quá trình đăng nhập bằng cách gọi phương thức login từ service
		    UserModel user = userService.login(username, password);

		    // Kiểm tra kết quả đăng nhập
		    if (user != null) {
		        // Nếu đăng nhập thành công, tạo phiên (session) và lưu thông tin người dùng vào
		        // session
		        HttpSession session = req.getSession(true);
		        session.setAttribute(Constant.userSession, user);

		        // Chuyển hướng đến trang "waiting" (hoặc trang chính của ứng dụng)
		        resp.sendRedirect(req.getContextPath() + "/waiting");
		    } else {
		    	// Nếu đăng nhập không thành công, đặt thông báo lỗi vào request
				req.setAttribute("loginError", "Thông tin đăng nhập không chính xác!");

				// Forward lại đến trang login.jsp để hiển thị thông báo lỗi
				req.getRequestDispatcher("/views/account/login.jsp").forward(req, resp);
		    }
		} catch (Exception e) {
		    // Nếu có lỗi xảy ra, chuyển hướng đến trang 404
		    resp.sendRedirect(req.getContextPath() + "/views/guest/404.jsp");
		}

//		// Lấy giá trị từ tham số request
//		String username = req.getParameter("username");
//		String password = req.getParameter("password");
//
//		// Kiểm tra xem tên đăng nhập và mật khẩu có được nhập không
//		if (username.isEmpty() || password.isEmpty()) {
//			// Nếu không nhập đầy đủ thông tin, chuyển hướng đến trang đăng nhập để nhập lại
//			req.getRequestDispatcher("/views/account/login.jsp").forward(req, resp);
//			return;
//		}
//
//		// Thực hiện quá trình đăng nhập bằng cách gọi phương thức login từ service
//		UserModel user = userService.login(username, password);
//
//		// Kiểm tra kết quả đăng nhập
//		if (user != null) {
//			// Nếu đăng nhập thành công, tạo phiên (session) và lưu thông tin người dùng vào
//			// session
//			HttpSession session = req.getSession(true);
//			session.setAttribute(Constant.userSession, user);
//
//			// Chuyển hướng đến trang "waiting" (hoặc trang chính của ứng dụng)
//			resp.sendRedirect(req.getContextPath() + "/waiting");
//		} else {
//			// Nếu đăng nhập không thành công, đặt thông báo lỗi vào request
//			req.setAttribute("loginError", "Thông tin đăng nhập không chính xác!");
//
//			// Forward lại đến trang login.jsp để hiển thị thông báo lỗi
//			req.getRequestDispatcher("/views/account/login.jsp").forward(req, resp);
//		}
	}

	private void postVerify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy phiên (session) hiện tại từ request
		HttpSession session = req.getSession();

		// Lấy thông tin người dùng và mã xác nhận từ session
		UserModel userModel = (UserModel) session.getAttribute("user");
		String codeSend = (String) session.getAttribute("code");

		// Lấy mã xác nhận từ tham số request
		String code = req.getParameter("verify-code");

		// Kiểm tra xem mã xác nhận nhập vào có khớp với mã đã gửi đi không
		if (code.equals(codeSend)) {
			// Nếu khớp, cập nhật trạng thái email đã được xác nhận và chuyển hướng đến
			// trang đăng nhập
			boolean isSuccess = userService.insertRegister(userModel);
			if (isSuccess) {
				userModel.setEmail(userModel.getEmail());
				userModel.setEmailActive(true);
				userService.updateStatusEmail(userModel);
				resp.sendRedirect(req.getContextPath() + "/login-customer");
			}
		} else {
			// Xử lý ngoại lệ nếu có lỗi xảy ra
			req.setAttribute("verifyError", "Thông tin xác thực không chính xác!");

			// Forward lại đến trang login.jsp để hiển thị thông báo lỗi
			req.getRequestDispatcher("/views/account/verify.jsp").forward(req, resp);
		}

	}

	public boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	private void postRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		// Cài đặt loại và bảng mã cho phản hồi từ server
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		// Lấy giá trị từ tham số request
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String firstName = req.getParameter("first-name");
		String lastName = req.getParameter("last-name");

		// Kiểm tra xem email có hợp lệ không
		if (!isValidEmail(email)) {
			// Nếu email không hợp lệ, chuyển hướng đến trang 404
			req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
			return;
		}

		// Kiểm tra xem email đã tồn tại trong hệ thống hay chưa
		if (userService.checkExistEmial(email)) {
			// Nếu email đã tồn tại, đặt thông báo lỗi vào request và chuyển hướng đến trang
			// đăng ký
			req.setAttribute("registrationError", "Email đã tồn tại");
			req.getRequestDispatcher("/views/account/register.jsp").forward(req, resp);
		} else {
			// Nếu email chưa tồn tại, tiếp tục quá trình đăng ký
			Email mail = new Email();

			// Tạo mã xác nhận ngẫu nhiên
			String code = mail.getRandom();

			// Tạo đối tượng UserModel để chứa thông tin người dùng
			UserModel user = new UserModel(firstName, lastName, email, password);

			// Gửi email xác nhận đăng ký và kiểm tra xem việc gửi email có thành công không
			boolean test = mail.sendEmail(user, code);

			if (test) {
				// Nếu gửi email thành công, tạo phiên (session) và lưu thông tin người dùng và
				// mã xác nhận vào session
				HttpSession session = req.getSession();
				session.setAttribute("user", user);
				session.setAttribute("code", code);

				// Nếu đăng ký thành công, chuyển hướng đến trang xác nhận đăng ký
				resp.sendRedirect(req.getContextPath() + "/verify-customer");

			} else {
				// Nếu gửi email thất bại, in thông báo lỗi ra response
				PrintWriter outPrintWriter = resp.getWriter();
				outPrintWriter.println("Lỗi khi gửi mail!");
			}
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
