package com.azshop.DAO;

import java.util.List;

import com.azshop.models.CategoryModel;

public interface ICategoryDAO {
	  void insert(CategoryModel category);
	    CategoryModel getById(int id);
	    List<CategoryModel> getAll();
	    List<CategoryModel> getChildCategory(int parentId);
	    List<CategoryModel> getParentCategory();
	    void update(CategoryModel category);
	    void delete(int id);
	    CategoryModel getCategoryBySlug(String slug);
}
