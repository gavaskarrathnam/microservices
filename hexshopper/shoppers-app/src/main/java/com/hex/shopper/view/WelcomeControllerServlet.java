package com.hex.shopper.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hex.shopper.util.HostHelper;

public class WelcomeControllerServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(WelcomeControllerServlet.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	//Get Host Name & port
	HostHelper helper = new HostHelper();
    Map<String, String> hostMap = helper.getIPAddress();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		showWelcome(writer, req);
		//resp.sendRedirect("welcome.jsp"); //welcome page
	}
	
	private void showWelcome(PrintWriter writer, HttpServletRequest req) {
		logger.info("***** START :: Enter into showWelcome ******");
		String line = null;
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add("header.jsp");
		pages.add("navigation.jsp");
		pages.add("banner.jsp");
		pages.add("welcome.jsp");
		pages.add("footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String relativeWebPath = (String) iterator.next();
			try {
				
				bufRedTop = new BufferedReader(new FileReader(helper.getRoot() + relativeWebPath));
				//bufRedTop = new BufferedReader(new FileReader(pgName));
				while( (line = bufRedTop.readLine()) != null ){
					
					line = line.toString().replace("localhost", hostMap.get("karaf.ipaddress"));
					line = line.replace("8181", hostMap.get("karaf.port"));
					
					writer.println( line );
				}
			} catch (IOException e ) {
				System.out.println("*** ERROR: JSP File Reading Issue");
				logger.error("*** ERROR: JSP File Reading Issue");
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
		logger.info("***** END :: Enter into showWelcome ******");		
	}
}
