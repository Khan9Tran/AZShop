package com.azshop.controllers.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import com.azshop.models.ProductModel;
import com.azshop.models.StoreModel;
import com.azshop.models.StyleModel;
import com.azshop.models.UserModel;
import com.azshop.services.CartItemServiceImpl;
import com.azshop.services.CartServiceImpl;
import com.azshop.services.CategoryServiceImpl;
import com.azshop.services.ICartItemService;
import com.azshop.services.ICartService;
import com.azshop.services.ICategoryService;
import com.azshop.services.IImageService;
import com.azshop.services.IProductService;
import com.azshop.services.IStoreService;
import com.azshop.services.IStyleService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.utils.CheckValid;
import com.azshop.utils.Constant;
import com.opensymphony.module.sitemesh.tapestry.Util;
import org.owasp.encoder.Encode;



@WebServlet(urlPatterns = {"/customer/search", "/guest/search"})
public class searchController extends HttpServlet {
		private static final long serialVersionUID = 1L;
		IProductService productService = new ProductServiceImpl();
		IStoreService storeService = new StoreServiceImpl();
		ICategoryService categoryService = new CategoryServiceImpl();
		IStyleService styleService = new StyleServiceImpl();
		IImageService imageService = new ImageServiceImpl();
		ICartService cartService = new CartServiceImpl();
		ICartItemService cartItemService = new CartItemServiceImpl();
	    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("X-Content-Type-Options", "nosniff");
			String url = request.getRequestURL().toString();
			
			if (url.contains("/customer/search")) {
					HttpSession session = request.getSession();
					UserModel userModel = (UserModel) session.getAttribute(Constant.userSession);
					request.setAttribute("user", userModel);
					if (userModel == null)
					{
						response.sendRedirect(request.getContextPath() + "/login-customer");
						return;
					}
					List<CartModel> cartList = cartService.getByUserId(userModel.getId());
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

				    request.setAttribute("sumPrice", sum);
					
					List<ImageModel> imageProductsInCart = new ArrayList<ImageModel>();

					for (ProductModel productModel : productsInCart) {
						ImageModel image = imageService.getImage(productModel.getId());
						imageProductsInCart.add(image);
					}
					
					request.setAttribute("role", "customer");
					request.setAttribute("quantity", cartItemList.size());
					request.setAttribute("imageProductsInCart", imageProductsInCart);	
					request.setAttribute("cartItemList", cartItemList);
					request.setAttribute("productsInCart", productsInCart);	
			}
			
			else if (url.contains("/guest/search")) request.setAttribute("role", "guest");
			
			String style = Encode.forHtml(request.getParameter("styleId"));
			String page = Encode.forHtml(request.getParameter("page"));
			String categoryIdTmp = Encode.forHtml(request.getParameter("categoryId"));
			String action = Encode.forHtml(request.getParameter("action"));
			int styleTmp = -1;
			try {
				if (style != null) {
					styleTmp = Integer.parseInt(style);
				}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			int pageNumber = 1;
			try {
				if (page != null)
					pageNumber = Integer.parseInt(page);
				}
			catch (Exception e) {
				// TODO: handle exception
			}
			if (action != null)
			{
				if (action.equals("left"))
				{
					if (pageNumber -1 >= 1)
						pageNumber = pageNumber -1;
				}
				if (action.equals("right"))
				{
					pageNumber = pageNumber + 1;
				}
			}
			String keyword = ""; 
			if (CheckValid.isValidInput(request.getParameter("searchTerm")))
				keyword = request.getParameter("searchTerm");
			keyword = Encode.forHtml(keyword);
	        int categoryId = 0;
	        try {
	        	categoryId = Integer.parseInt(categoryIdTmp);
			} catch (Exception e) {
				// TODO: handle exception
			}
	        List<ProductModel> productModels = productService.search(keyword, categoryId, -1, -1, styleTmp,pageNumber,6);
	        if (productModels.size() == 0 && pageNumber!= 1)
	        {
	        	pageNumber = pageNumber -1;
	        	productModels = productService.search(keyword, categoryId, -1, -1, styleTmp,pageNumber,6);
	        }
	        request.setAttribute("page", pageNumber);
	        List<StoreModel> storeModels = storeService.searchByKey(keyword, -1);

			if (storeModels != null && !storeModels.isEmpty()) {
			    int limit = Math.min(3, storeModels.size());
			    List<StoreModel> selectedStores = new ArrayList<>();
			
			    for (int i = 0; i < limit; i++) {
			        selectedStores.add(storeModels.get(i));
			    }
			
			    request.setAttribute("stores", selectedStores);
			} else {
				
				
			    request.setAttribute("stores", Collections.emptyList());
			}
	        
	        List<StyleModel> styleModels = styleService.getAll();
	        request.setAttribute("images", styleModels);
	        List<ImageModel> imageModels = new ArrayList<ImageModel>();
	        for (ProductModel productModel : productModels) {
	        	imageModels.add(imageService.getImage(productModel.getId()));
			}
	        
//	        List<CategoryModel> categoryParentList = categoryService.getParentCategory();
//	        List<CategoryModel> categoryList = new ArrayList<CategoryModel>();
	        
	        List<CategoryModel> categoryList = categoryService.getAll();
	        
//	        for (CategoryModel categoryModel : categoryParentList) {
//				categoryList.addAll(categoryService.getChildCategory(categoryModel.getId()));
//			}
	        
	        for (CategoryModel category : categoryList) {
				
				category.setCountProduct(countProductsInCategory(category.getId()));
			}
	        
	        request.setAttribute("styleId", styleTmp);
	        request.setAttribute("images", imageModels);
	        request.setAttribute("styles", styleModels);
	        request.setAttribute("products", productModels);
	        request.setAttribute("searchTerm", keyword);
	        request.setAttribute("categoryId", categoryId);
	        request.setAttribute("categoryParentList", categoryList);
	        
	        
	        request.getRequestDispatcher("/views/customer/search.jsp").forward(request, response);
	    }
	    
	  //Đếm sản phẩm có trong danh mục
    	public int countProductsInCategory(int categoryId) {
            List<ProductModel> productList = productService.getByCategoryId(categoryId);

            int productCount = (productList != null) ? productList.size() : 0;

            return productCount;
        }

}


