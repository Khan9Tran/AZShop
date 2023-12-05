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

@WebServlet(urlPatterns = {"/guest-home", "/guest/category/*", "/guest/product", "/guest-search"})
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
		
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);
		
		if (url.contains("guest-home")) {
			try {
				getAllProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (url.contains("guest/category")) {
			try {
				getCategory(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (url.contains("guest/product")) {
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
	
	public int countProductsInCategory(int categoryId) {
        // Get products by category
        List<ProductModel> productList = productService.getByCategoryId(categoryId);

        // Count the number of products
        int productCount = (productList != null) ? productList.size() : 0;

        return productCount;
    }
	
	private void getCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		if (url.contains("/category")) {
			String pageCurrent = "category";
			URI uri;
			try {
	
				uri = new URI(url);
				String path = uri.getPath();
				System.out.println("Extracted path: " + path);
				
				String[] parts = path.split("/");
	
				if (parts.length > 0) {
					String slug = parts[parts.length - 1];
					req.setAttribute("slug", slug);
					pageCurrent = pageCurrent + slug;
	
					// Print the slug to the console for debugging
					System.out.println("Extracted Slug: " + slug);
					
					try {
		                CategoryModel categoryParent = categoryService.getCategoryBySlug(slug);

		                if (categoryParent == null) {
		                    req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
		                } else {

		                    List<CategoryModel> categoryChildList = categoryService.getChildCategory(categoryParent.getId());
		                    
		                    for (CategoryModel categoryChild : categoryChildList) {
								int countProduct = countProductsInCategory(categoryChild.getId());
								
								categoryChild.setCountProduct(countProduct);
							}

		                    req.setAttribute("categoryChildList", categoryChildList);
		                }
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
					
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			RequestDispatcher rd = req.getRequestDispatcher("/views/guest/category.jsp");
            rd.forward(req, resp);
		}
	}

	private void findProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		// nhan du lieu tu form
		String keyword = req.getParameter("keyword");
		
		List<ProductModel> productList = productService.FindProduct(keyword);
		
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
		StyleValueModel styleValue = styleValueService.getById(product.getStyleValueId());
		List<ImageModel> imageList = imageService.getByProductId(id);
		
		//san pham lien quan
		List<ProductModel> productRelateds = productService.getByCategoryId(product.getCategoryId());
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