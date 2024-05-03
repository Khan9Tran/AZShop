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
import com.azshop.models.ProductModel;
import com.azshop.models.StoreModel;
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
import com.azshop.services.IStyleValueService;
import com.azshop.services.IUserService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.services.ProductServiceImpl;
import com.azshop.services.StoreServiceImpl;
import com.azshop.services.StyleServiceImpl;
import com.azshop.services.StyleValueImpl;
import com.azshop.services.UserServiceImpl;
import com.azshop.utils.Constant;

@WebServlet(urlPatterns = {"/customer/store/*", "/guest/store/*"})
public class StoreController extends HttpServlet{
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
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURI().toString();
		resp.setHeader("X-Content-Type-Options", "nosniff");
		
		//Hiển thị menu danh mục
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
		
		URI uri;
		try {

			uri = new URI(url);
			String path = uri.getPath();

			String[] parts = path.split("/");

			if (parts.length > 0) {
				String slug = parts[parts.length - 1];
				if(!isValidSlug(slug)) {
					RequestDispatcher rd = req.getRequestDispatcher("/views/guest/404.jsp");
					rd.forward(req, resp);
					return;
				}
				
				try {
					StoreModel store = storeService.getBySlug(slug);
					if(!isValidSlug(req.getParameter("cate"))) {
						RequestDispatcher rd = req.getRequestDispatcher("/views/guest/404.jsp");
						rd.forward(req, resp);
						return;
					}
					CategoryModel category = categoryService.getCategoryBySlug(req.getParameter("cate"));
					
					
					List<ProductModel> productList = new ArrayList<ProductModel>();
	                List<ImageModel> imageList = new ArrayList<ImageModel>();
	                List<CategoryModel> categoryChildList = new ArrayList<CategoryModel>();
	                
	                //Lấy tất cả sản phẩm trong giỏ hàng
	                productList = productService.getByStoreId(store.getId());
	                
	              //lấy danh sách danh mục
	                for (ProductModel productModel : productList) {
						CategoryModel categoryChild = categoryService.getById(productModel.getCategoryId());
						boolean isExistCategoryChild = false;
						for (CategoryModel categoryModel : categoryChildList) {
							if (categoryModel.getId() == categoryChild.getId()) {
								isExistCategoryChild = true;
							}
						}
						if (isExistCategoryChild == false) {
							categoryChildList.add(categoryChild);
						}
					}	                           
	                
	              //đếm số lượng product trong mỗi category
	                for (CategoryModel categoryChild : categoryChildList) {
						int countProduct = countProductsInCategoryStore(store.getId(), categoryChild.getId());
						
						categoryChild.setCountProduct(countProduct);
					}
	                
	                //Lấy sản phẩm trong danh mục nằm trong cửa hàng
	                List<ProductModel> productsInCate = new ArrayList<ProductModel>();
	                if (category.getSlug() != null) {
	                	for (ProductModel product : productList) {
							if (product.getCategoryId() == category.getId()) {
								productsInCate.add(product);
							}
						}
	                	productList = productsInCate;
	                	req.setAttribute("category", category);
	                }	                	                
	                
	                for (ProductModel productModel : productList) {
	        			ImageModel image = imageService.getImage(productModel.getId());
	        			imageList.add(image);
	        		}
	                
	                
	                List<ProductModel> productListSort = new ArrayList<ProductModel>();             
	                
	                int sortBy = 0;
	                try {
						String sortby = req.getParameter("sortBy");
						if (sortby == null || (!sortby.equals("0") && !sortby.equals("1"))) {
				            // Chuyển hướng người dùng đến trang error
				        	req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
				            return; // Kết thúc phương thức để ngăn chặn việc tiếp tục xử lý
				        }
						
						sortBy = Integer.parseInt(req.getParameter("sortBy"));

					} catch (Exception e) {
						e.printStackTrace();
						RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
						rd.forward(req, resp);
					}
	                
	              //sắp xếp
	                if (sortBy == 0) {
		                productListSort = productService.SortingProductbyPriceAscending(productList);
	                }
	                else if (sortBy == 1) {
	                	productListSort = productService.SortingProductbyPriceDecending(productList);		               
	                }
	                
	                List<ProductModel> hotProductList = productService.getHotProduct(store.getId());
	                List<ImageModel> imageModels = new ArrayList<ImageModel>();
	                
	                for (ProductModel productModel : hotProductList) {
	        			ImageModel image = imageService.getImage(productModel.getId());
	        			imageModels.add(image);
	        		}	  
	                
	                req.setAttribute("hotProductList",hotProductList);
	                req.setAttribute("imageProHotList", imageModels);
	                req.setAttribute("sortBy", sortBy);
	                req.setAttribute("store", store);	               
	                req.setAttribute("categoryChildList", categoryChildList);
	                req.setAttribute("productList", productListSort);
	                req.setAttribute("imageList", imageList);
	               	                
	                
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/store.jsp");
        rd.forward(req, resp);
	}
	
	public int countProductsInCategoryStore(int storeId, int categoryId) {
        // Get products by category
        List<ProductModel> productsInCate = productService.getByCategoryId(categoryId);
        List<ProductModel> productList = new ArrayList<ProductModel>();
        
        for (ProductModel productModel : productsInCate) {
			if (productModel.getStoreId() == storeId) {
				productList.add(productModel);
			}
		}
        
        // Count the number of products
        int productCount = (productList != null) ? productList.size() : 0;

        return productCount;
    }
	
	public boolean isValidSlug(String slug) {
		if (slug == null) {
			return false;
		}

		if (!slug.matches("^[a-zA-Z0-9_-]+$")) {
			return false;
		}

		if (slug.contains("--")) {
			return false;
		}

		return true;
	}
}
