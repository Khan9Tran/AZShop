package com.azshop.services;

import java.util.List;

import com.azshop.DAO.IUserDAO;
import com.azshop.DAO.UserDAOImpl;
import com.azshop.models.UserModel;

public class UserServiceImpl implements IUserService{

	IUserDAO userDAO = new UserDAOImpl();
	@Override
	public void insert(UserModel user) {
		userDAO.insert(user);
	}

	@Override
	public void update(UserModel user) {
		userDAO.update(user);
	}

	@Override
	public void delete(int id) {
		userDAO.delete(id);
	}

	@Override
	public List<UserModel> getAll() {
		return userDAO.getAll();
	}

	@Override
	public UserModel getById(int id) {
		return userDAO.getById(id);
	}

	@Override
	public boolean checkExistEmial(String email) {
		return userDAO.checkExistEmial(email);
	}

	@Override
	public boolean insertRegister(String firstName, String lastName, String email, String password) {
		if (userDAO.checkExistEmial(email)) {
			return false;
		}
		userDAO.insertRegister(firstName, lastName, email, password);
		return true;
	}

	@Override
	public void updateStatusEmail(UserModel user) {
		userDAO.updateStatusEmail(user);
	}

}