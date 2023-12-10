package com.azshop.controllers.customer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.azshop.DAO.CartItemDAOImpl;
import com.azshop.DAO.CategoryDAOImpl;
import com.azshop.models.CartItemModel;
import com.azshop.models.CartModel;
import com.azshop.models.CategoryModel;
import com.azshop.models.DeliveryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.ProductModel;
import com.azshop.models.StoreModel;
import com.azshop.models.StyleModel;
import com.azshop.models.StyleValueModel;
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

@WebServlet(urlPatterns = {"/customer-home", "/customer-information", "/customer/hot-product"})
public class CustomerController extends HttpServlet {
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
		
		//Hiển thị menu danh mục
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);				
		
		HttpSession sessionCart = req.getSession();
		
		UserModel userCart = (UserModel) sessionCart.getAttribute(Constant.userSession);
		req.setAttribute("user", userCart);
		if (userCart == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login-customer");
			return;
		}
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
		
		req.setAttribute("quantity", cartItemList.size());
		req.setAttribute("user", userCart);
		req.setAttribute("imageProductsInCart", imageProductsInCart);	
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("productsInCart", productsInCart);		
		
		if (url.contains("customer-home")) {
			try {
				getAll(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
		
		else if (url.contains("customer/store/")) {
			try {
				getStore(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if (url.contains("customer/hot-product")) {
			try {
				getHotProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if (url.contains("customer-search")) {
			try {
				search(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
		else if (url.contains("customer/product")) {
			try {
				getProduct(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		else if (url.contains("customer-information")) {
			try {
				HttpSession session = req.getSession();
				if (session != null) {
					Object sessionObject = session.getAttribute(Constant.userSession);
					if (sessionObject instanceof UserModel) {
						UserModel user = (UserModel) sessionObject;
						req.setAttribute("user", user);
						// Sử dụng thông tin người dùng ở đây
					}
				}
				getInforCustomer(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getHotProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<ProductModel> productList = productService.getAll();
		List<CategoryModel> categoryList = categoryService.getAll();
		List<ProductModel> productHotList = productService.GetTopSellerProduct(productList, 10);
		List<ImageModel> imageList = new ArrayList<ImageModel>();
		
		for (ProductModel productModel : productHotList) {
			ImageModel image = imageService.getImage(productModel.getId());
			imageList.add(image);
		}
		
		req.setAttribute("imageList", imageList);
		req.setAttribute("categoryList", categoryList);
		req.setAttribute("productList",productHotList);
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/HotProduct.jsp");
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

	private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ma hoa UTF-8
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		// nhan du lieu tu form
		String keyword = req.getParameter("keyword");

		List<ProductModel> productList = productService.FindProduct(keyword);
		List<StoreModel> storeList = storeService.FindStore(keyword);
		List<CategoryModel> categoryList = categoryService.FindCategory(keyword);

		if (productList.size() != 0) {
			List<CategoryModel> categorys = categoryService.getAll();
			List<ImageModel> imageList = new ArrayList<ImageModel>();

			for (ProductModel productModel : productList) {
				ImageModel image = imageService.getImage(productModel.getId());
				imageList.add(image);
			}

			req.setAttribute("productList", productList);
			req.setAttribute("categoryList", categorys);
			req.setAttribute("imageList", imageList);
			RequestDispatcher rd = req.getRequestDispatcher("/views/customer/SearchProduct.jsp");
			rd.forward(req, resp);
		}

		else {
			// Tìm danh mục
			if (storeList.size() != 0) {
				req.setAttribute("storeList", storeList);
				RequestDispatcher rd = req.getRequestDispatcher("/views/customer/SearchStore.jsp");
				rd.forward(req, resp);
			} else {
				// Tìm danh mục
				if (categoryList.size() != 0) {
					req.setAttribute("categoryList", categoryList);
					RequestDispatcher rd = req.getRequestDispatcher("/views/customer/SearchCategory.jsp");
					rd.forward(req, resp);
				}
			}
		}

	}

	private void getStore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		URI uri;
		try {

			uri = new URI(url);
			String path = uri.getPath();

			String[] parts = path.split("/");

			if (parts.length > 0) {
				String slug = parts[parts.length - 1];
				
				try {

					StoreModel store = storeService.getBySlug(slug);
					CategoryModel category = categoryService.getCategoryBySlug(req.getParameter("cate"));
					
					List<ProductModel> productList = new ArrayList<ProductModel>();
	                List<ImageModel> imageList = new ArrayList<ImageModel>();
	                List<CategoryModel> categoryChildList = new ArrayList<CategoryModel>();
	                
	                //Lấy tất cả sản phẩm trong giỏ hàng
	                productList = productService.getByStoreId(store.getId());
	                
	              //lấy danh sách danh mục
	                for (ProductModel productModel : productList) {
						CategoryModel categoryChild = categoryService.getById(productModel.getCategoryId());
						categoryChildList.add(categoryChild);
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
	                
	                int sortBy = Integer.parseInt(req.getParameter("sortBy"));
	                
	                if (sortBy == 0) productList = productService.SortingProductbyPriceAscending(productList);
	                else productList = productService.SortingProductbyPriceDecending(productList);
	                
	                req.setAttribute("store", store);	               
	                req.setAttribute("categoryChildList", categoryChildList);
	                req.setAttribute("productList", productList);
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

	private void getInforCustomer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/views/account/information.jsp").forward(req, resp);
	}

	private void getAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<ProductModel> productList = productService.getAll();
		List<CategoryModel> categoryList = categoryService.getAll();
		List<ImageModel> imageList = new ArrayList<ImageModel>();
		
		for (ProductModel productModel : productList) {
			ImageModel image = imageService.getImage(productModel.getId());
			imageList.add(image);
		}
		
		req.setAttribute("imageList", imageList);
		req.setAttribute("categoryList", categoryList);
		req.setAttribute("productList",productList);
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/home.jsp");
		rd.forward(req, resp);
	}

	//Đếm sản phẩm có trong danh mục
	public int countProductsInCategory(int categoryId) {
        List<ProductModel> productList = productService.getByCategoryId(categoryId);

        int productCount = (productList != null) ? productList.size() : 0;

        return productCount;
    }

	private void getProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		String pageCurrent = "category";
		URI uri;
		try {

			uri = new URI(url);
			String path = uri.getPath();

			String[] parts = path.split("/");

			if (parts.length > 0) {
				String slug = parts[parts.length - 1];
				req.setAttribute("slug", slug);
				pageCurrent = pageCurrent + slug;
				
				try {
					ProductModel product = productService.getBySlug(slug);
					CategoryModel category = categoryService.getById(product.getCategoryId());	
					StyleValueModel styleValue = styleValueService.getById(product.getStyleValueId());
					List<ImageModel> imageList = imageService.getByProductId(product.getId());
					StoreModel store = storeService.getById(product.getStoreId());
					
					//san pham lien quan
					List<ProductModel> productRelateds = productService.getByCategoryId(product.getCategoryId());
					List<ImageModel> imageRelateds = new ArrayList<ImageModel>();
					for (ProductModel productRelated : productRelateds) {
						ImageModel imageRelate = imageService.getImage(productRelated.getId());
						imageRelateds.add(imageRelate);
					}
	                
					req.setAttribute("product", product);
					req.setAttribute("store", store);
					req.setAttribute("productRelateds", productRelateds);
					req.setAttribute("category", category);
					req.setAttribute("styleValue", styleValue);
					req.setAttribute("imageList", imageList);
					req.setAttribute("imageRelateds", imageRelateds);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/product.jsp");
		rd.forward(req, resp);
	}
}
