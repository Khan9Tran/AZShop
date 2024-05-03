package com.azshop.controllers.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.owasp.encoder.Encode;

import com.azshop.utils.Constant;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/video"})
public class VideoController extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		 String fileName = Encode.forHtml(req.getParameter("fname"));
		    File file = new File(Constant.DIR + "/" + fileName);
		    System.out.println("File path: " + file.getAbsolutePath());

		    String mimeType = "video/mp4";
		    resp.setContentType(mimeType);

		    try {
		        if (file.exists()) {
		            IOUtils.copy(new FileInputStream(file), resp.getOutputStream());
		        } else {
		            System.out.println("File not found: " + file.getAbsolutePath());
		            req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        req.getRequestDispatcher("/views/guest/404.jsp").forward(req, resp);
		    }
	}
}
