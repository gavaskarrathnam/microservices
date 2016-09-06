package net.lr.orders.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.File;
import java.io.FileReader;


public class WelcomeControllerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		showWelcome(writer);
		//resp.sendRedirect("welcome.jsp"); //welcome page
	}
	
	private void showWelcome(PrintWriter writer) {
		String line = "";
		String lineBtm = "";
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/header.jsp");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/navigation.jsp");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/banner.jsp");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/welcome.jsp");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String pgName = (String) iterator.next();
			try {
				bufRedTop = new BufferedReader(new FileReader(pgName));
				while( (line = bufRedTop.readLine()) != null ){
					writer.println( line.toString() );
				}
			} catch (IOException e ) {
				System.out.println("*** ERROR: Reading CSV File Issue");
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
