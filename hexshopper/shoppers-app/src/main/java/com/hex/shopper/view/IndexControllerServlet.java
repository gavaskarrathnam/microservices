package com.hex.shopper.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class IndexControllerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		service(req, resp);
	}

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		showWelcome(writer, request);
		//resp.sendRedirect("welcome.jsp"); //welcome page
	}
	
	private void showWelcome(PrintWriter writer, HttpServletRequest req) {
		
		String jspPath = "E:/workdesk/hexshopper/shoppers-app/src/main/webapp/pages/";
		
		String line = null;
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add(jspPath + "header.jsp");
		pages.add(jspPath + "navigation.jsp");
		pages.add(jspPath + "banner.jsp");
		pages.add(jspPath + "welcome.jsp");
		pages.add(jspPath + "footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String relativeWebPath = (String) iterator.next();
			try {
				
				InputStream file = IndexControllerServlet.class.getResourceAsStream(relativeWebPath);
				bufRedTop = new BufferedReader(new InputStreamReader(file));
				
				while( (line = bufRedTop.readLine()) != null ){
					writer.println( line.toString() );
				}
			} catch (IOException e ) {
				System.out.println("*** ERROR: JSP File Reading Issue");
			}finally {
				if (bufRedTop != null) {
					try {
						bufRedTop.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
}
