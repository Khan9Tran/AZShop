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

import org.w3c.dom.stylesheets.StyleSheetList;

import com.azshop.models.CartItemModel;
import com.azshop.models.CartModel;
import com.azshop.models.CategoryModel;
import com.azshop.models.ImageModel;
import com.azshop.models.ProductModel;
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

@WebServlet(urlPatterns = { "/guest/category/*", "/customer/category/*" })
public class CategoryController extends HttpServlet {
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
		// Hiển thị menu danh mục
		List<CategoryModel> categoryParentList = categoryService.getParentCategory();
		req.setAttribute("categoryParentList", categoryParentList);

		if (url.contains("customer")) {
			HttpSession sessionCart = req.getSession();
			UserModel userCart = (UserModel) sessionCart.getAttribute(Constant.userSession);

			List<CartModel> cartList = cartService.getByUserId(userCart.getId());
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

				try {
					// Lấy category từ slug
					CategoryModel category = null;
					if (isValidSlug(slug)) {
						try {
							category = categoryService.getCategoryBySlug(slug);
						} catch (Exception e) {
							e.printStackTrace();
							RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
							rd.forward(req, resp);
							return;
						}
					} else {
						RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
						rd.forward(req, resp);
						return;
					}

					if (category == null) {
						RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
						rd.forward(req, resp);
						return;
					}

					Boolean isCategoryOrigin = false;

					// Kiếm tra category có phải là một caetgory gốc
					for (CategoryModel categoryModel : categoryParentList) {
						if (category.getId() == categoryModel.getId()) {
							isCategoryOrigin = true;
						}
					}

					// Khai báo trong trường hợp nó là category gốc
					CategoryModel categoryParent = categoryService.getParentCategory(category.getId());
					List<CategoryModel> categoryChildList = categoryService.getChildCategory(category.getId());

					List<ProductModel> productList = new ArrayList<ProductModel>();
					List<ImageModel> imageList = new ArrayList<ImageModel>();

					// Lấy ra tất cả sản phẩm
					for (CategoryModel categoryChild : categoryChildList) {
						List<ProductModel> productCategoryChilds = productService
								.getByCategoryId(categoryChild.getId());
						productList.addAll(productCategoryChilds);
					}

					// nếu không phải
					if (isCategoryOrigin == false) {
						categoryChildList = categoryService.getChildCategory(category.getCategoryId());
						categoryParent = categoryService.getById(category.getCategoryId());
						productList.clear();
						productList = productService.getByCategoryId(category.getId());

						// Lấy danh sách style value từ category parent
						List<StyleModel> styleList = styleService.getByCateId(category.getId());

						req.setAttribute("styleList", styleList);

						String styleIdString = req.getParameter("styleId");

						if (styleIdString != null) {
							int styleId = Integer.parseInt(styleIdString);
							if (styleId != 0) {
								List<StyleValueModel> styleValueList = styleValueService.getByStyleId(styleId);
								productList.clear();
								for (StyleValueModel styleValue : styleValueList) {
									List<ProductModel> productsInStyle = productService
											.getByStyleValueId(styleValue.getId());
									productList.addAll(productsInStyle);
								}
							}
						}

					}

					// đếm số lượng product trong mỗi category
					for (CategoryModel categoryChild : categoryChildList) {
						int countProduct = countProductsInCategory(categoryChild.getId());

						categoryChild.setCountProduct(countProduct);
					}

					List<ProductModel> productListSort = new ArrayList<ProductModel>();

					List<ProductModel> hotProductList = productService.GetTopSellerProduct(productList, 3);
					List<ImageModel> imageModels = new ArrayList<ImageModel>();

					for (ProductModel productModel : hotProductList) {
						ImageModel image = imageService.getImage(productModel.getId());
						imageModels.add(image);
					}

					int sortBy = 0;
					try {
						sortBy = Integer.parseInt(req.getParameter("sortBy"));

					} catch (Exception e) {
						e.printStackTrace();
						RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
						rd.forward(req, resp);
					}

					// sắp xếp
					if (sortBy == 0) {
						productListSort = productService.SortingProductbyPriceAscending(productList);
					} else if (sortBy == 1) {
						productListSort = productService.SortingProductbyPriceDecending(productList);
					}

					for (ProductModel productModel : productList) {
						ImageModel image = imageService.getImage(productModel.getId());
						imageList.add(image);
					}

					req.setAttribute("hotProductList", hotProductList);
					req.setAttribute("imageProHotList", imageModels);
					req.setAttribute("sortBy", sortBy);
					req.setAttribute("category", category);
					req.setAttribute("categoryChildList", categoryChildList);
					req.setAttribute("categoryList", categoryChildList);
					req.setAttribute("productList", productListSort);
					req.setAttribute("imageList", imageList);
					req.setAttribute("categoryParent", categoryParent);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			RequestDispatcher rd = req.getRequestDispatcher("/views/vendor/404.jsp");
			rd.forward(req, resp);
		}

		RequestDispatcher rd = req.getRequestDispatcher("/views/customer/category.jsp");
		rd.forward(req, resp);

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

	public int countProductsInCategory(int categoryId) {
		List<ProductModel> productList = productService.getByCategoryId(categoryId);

		int productCount = (productList != null) ? productList.size() : 0;

		return productCount;
	}
}