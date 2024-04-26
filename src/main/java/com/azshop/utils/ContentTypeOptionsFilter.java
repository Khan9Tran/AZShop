package com.azshop.utils;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ContentTypeOptionsFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        chain.doFilter(request, response);
    }

    public void destroy() {}
}