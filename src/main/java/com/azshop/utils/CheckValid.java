package com.azshop.utils;

public class CheckValid {
	public static boolean isValidPhoneNumber(String phoneNumber) {
	    String regex = "^[0-9]{10}$"; 
	    return phoneNumber.matches(regex);
	}
	public static Boolean isValidInput(String searchTerm)
	{
		if (searchTerm != null && searchTerm.matches("^[a-zA-Z0-9]*$")) {
		    // Dữ liệu hợp lệ
			return true;
		}
		return false;
	}
}
