package com.azshop.controllers.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.azshop.services.IImageService;
import com.azshop.services.ImageServiceImpl;
import com.azshop.utils.Constant;
import com.google.gson.JsonObject;
import org.owasp.encoder.Encode;


@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/image"})
public class ImageController extends HttpServlet {
	IImageService imageService = new ImageServiceImpl();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");
       try {
    	   String fileName = Encode.forHtml(req.getParameter("fname"));
           File file = new File(Constant.DIR + "/" + fileName);
           resp.setContentType("image/jpeg");

           if (file.exists()) {
               IOUtils.copy(new FileInputStream(file), resp.getOutputStream());
           } else 
           {
        	   System.out.println("File not found: " + fileName);
        	   resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
           } 
       }
       	catch (Exception e) {
				System.out.println("File not found ");
		
		        // Trả về lỗi 404 cho người dùng
		        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
		}
       }

}
