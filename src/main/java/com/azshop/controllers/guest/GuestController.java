package com.azshop.controllers.guest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.azshop.DAO.CategoryDAOImpl;
import com.azshop.models.CategoryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.ProductModel;
import com.azshop.models.StyleModel;
import com.azshop.models.StyleValueModel;
import com.azshop.models.UserModel;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.ICartService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IImageService;
import com.azshop.services.IProductService;
import com.azshop.services.IStyleService;
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;
import com.azshop.utils.Email;

@WebServlet(urlPatterns = {"/guest-home", "/guest-clothing", "/guest-product", "/guest-search"})
public class GuestController extends HttpServlet{

	private static final long serialVersionUID = 1L;

	ICategoryService categoryService = new CategoryServiceImpl();
	IProductService productService = new ProductServiceImpl();
	IStyleService styleService = new StyleServiceImpl();
	IStyleValueService styleValueService = new StyleValueImpl();
	IImageService imageService = new ImageServiceImpl();
	IUserService userService = new UserServiceImpl();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		if (url.contains("guest-home")) {
			try {
				getAllProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (url.contains("guest-clothing")) {
			try {
				getAllClothing(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (url.contains("guest-product")) {
			try {
				getProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (url.contains("guest-search")) {
			try {
				findProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void findProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		// nhan du lieu tu form
		String keyword = req.getParameter("keyword");
		
		List<ProductModel> productList = productService.FindProduct(keyword);
		
//		req.setAttribute("product",product);
		req.setAttribute("productList",productList);
		
		RequestDispatcher rd = req.getRequestDispatcher("/views/guest/home.jsp");
		rd.forward(req, resp);
	}

	private void getProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		// nhan du lieu tu form
		int id = Integer.parseInt(req.getParameter("id"));
		
		ProductModel product = productService.getById(id);
		CategoryModel category = categoryService.getById(product.getCategoryId());
		List<ProductModel> productRelateds = productService.getByCategoryId(product.getCategoryId());
		StyleValueModel styleValue = styleValueService.getById(product.getStyleValueId());
		List<ImageModel> imageList = imageService.getByProductId(id);
		List<ImageModel> imageRelateds = new ArrayList<ImageModel>();
		for (ProductModel productRelated : productRelateds) {
			ImageModel imageRelate = imageService.getImage(productRelated.getId());
			imageRelateds.add(imageRelate);
		}
		
		req.setAttribute("product", product);
		req.setAttribute("productRelateds", productRelateds);
		req.setAttribute("category", category);
		req.setAttribute("styleValue", styleValue);
		req.setAttribute("imageList", imageList);
		req.setAttribute("imageRelateds", imageRelateds);
		
		RequestDispatcher rd = req.getRequestDispatcher("/views/guest/product.jsp");
		rd.forward(req, resp);
	}

	private void getAllClothing(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<ProductModel> productList = productService.getAll();
		
//		req.setAttribute("product",product);
		req.setAttribute("productList",productList);
		
		RequestDispatcher rd = req.getRequestDispatcher("/views/guest/clothing.jsp");
		rd.forward(req, resp);
	}


	private void getAllProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<CategoryModel> categoryList = categoryService.getAll();
		List<ProductModel> productList = productService.getAll();
		List<ImageModel> imageList = new ArrayList<ImageModel>();
		
		for (ProductModel productModel : productList) {
			ImageModel image = imageService.getImage(productModel.getId());
			imageList.add(image);
		}
		
		req.setAttribute("categoryList", categoryList);
		req.setAttribute("imageList", imageList);
		req.setAttribute("productList",productList);
		RequestDispatcher rd = req.getRequestDispatcher("/views/guest/home.jsp");
		rd.forward(req, resp);
	}
}