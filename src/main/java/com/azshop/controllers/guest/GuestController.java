package com.azshop.controllers.guest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Session;
import javax.mail.Store;
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
import com.azshop.models.StoreModel;
import com.azshop.models.StyleModel;
import com.azshop.models.StyleValueModel;
import com.azshop.models.UserModel;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.ICartService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IImageService;
import com.azshop.services.IProductService;
import com.azshop.services.IStoreService;
import com.azshop.services.IStyleService;
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;
import com.azshop.utils.Email;

@WebServlet(urlPatterns = {"/guest-home"})
public class GuestController extends HttpServlet{

	private static final long serialVersionUID = 1L;

	ICategoryService categoryService = new CategoryServiceImpl();
	IProductService productService = new ProductServiceImpl();
	IStyleService styleService = new StyleServiceImpl();
	IStyleValueService styleValueService = new StyleValueImpl();
	IImageService imageService = new ImageServiceImpl();
	IUserService userService = new UserServiceImpl();
	IStoreService storeService = new StoreServiceImpl();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		
		//Hiển thị menu danh mục
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);
		
		req.setAttribute("role", "guest");	
		if (url.contains("guest-home")) {
			try {
				getAll(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

	private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		// nhan du lieu tu form
		String keyword = req.getParameter("keyword");
		
		List<ProductModel> productList = productService.FindProduct(keyword);
		List<StoreModel> storeList = storeService.FindStore(keyword);
		List<CategoryModel> categoryList = categoryService.FindCategory(keyword);
		
		if (productList.size()!=0){		
			List<CategoryModel> categorys = categoryService.getAll();
			List<ImageModel> imageList = new ArrayList<ImageModel>();
			
			for (ProductModel productModel : productList) {
				ImageModel image = imageService.getImage(productModel.getId());
				imageList.add(image);
			}
			
			req.setAttribute("productList",productList);
			req.setAttribute("categoryList", categorys);
			req.setAttribute("imageList", imageList);
			RequestDispatcher rd = req.getRequestDispatcher("/views/guest/SearchProduct.jsp");
			rd.forward(req, resp);
		}
		
		else {
			//Tìm danh mục
			if (storeList.size()!=0) {
				req.setAttribute("storeList", storeList);
				RequestDispatcher rd = req.getRequestDispatcher("/views/guest/SearchStore.jsp");
				rd.forward(req, resp);
			}
			else {
				//Tìm danh mục
				if (categoryList.size()!=0) {
					req.setAttribute("categoryList", categoryList);
					RequestDispatcher rd = req.getRequestDispatcher("/views/guest/SearchCategory.jsp");
					rd.forward(req, resp);
				}		
			}
		}		
		
	}

	private void getAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<CategoryModel> categoryList = categoryService.getAll();
		List<ProductModel> productList = productService.getAll();
		List<ImageModel> imageList = new ArrayList<ImageModel>();
		List<ProductModel> newestProductList = productService.getNewestProduc(productList);
		
		for (ProductModel productModel : productList) {
			ImageModel image = imageService.getImage(productModel.getId());
			imageList.add(image);
		}
		
		req.setAttribute("categoryList", categoryList);
		req.setAttribute("imageList", imageList);
		req.setAttribute("productList",productList);
		req.setAttribute("newestProductList", newestProductList);
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/home.jsp");
		rd.forward(req, resp);
	}
}