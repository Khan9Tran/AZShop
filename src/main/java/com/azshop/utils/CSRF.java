package com.azshop.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRF {
	public static String getToken() throws NoSuchAlgorithmException{
		
	    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	    byte[] data = new byte[16];
	    secureRandom.nextBytes(data);

	    return Base64.getEncoder().encodeToString(data);
	}
}