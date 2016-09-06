package net.lr.orders.ui;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.FileReader;

import net.lr.orders.model.Order;
import net.lr.orders.model.OrderService;

public class OrderControllerServlet extends HttpServlet {
	OrderService orderService;
	String path = "E:/workdesk/orders/orders-view/src/main/webapp/";

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		String orderId = req.getParameter("orderId");
		String custInfo = getCustomerMicroService();
		PrintWriter writer = resp.getWriter();
		constructPage(writer, orderId, custInfo);
	}

	private void showOrderList(PrintWriter writer, String custInfo) {
		
		writer.println("<h1 class='header center orange-text'>Order Details</h1>");
		
		writer.println("<table class='bordered'>");
		writer.println("<tr><thead><td data-field='orderId'> Order Id </td><td data-field='orderDate'> Order Date </td> <td data-field='prodName'>Product Name</td> <td data-field='prodDesc'> Description </td> <td data-field='prodType'> Product Type </td> ");
		if(custInfo != null && !custInfo.isEmpty()){
			writer.println("<td data-field='custName'> Customer Name </td> <td data-field='custPhone'> Customer Phone# </td> <td data-field='custEmail'> Customer E-mail </td>");
		}
		writer.println("</tr><thead><tbody>");
		Collection<Order> orders = orderService.getOrder();
		SimpleDateFormat sdf = new SimpleDateFormat();
		for (Order order : orders) {
			writer.println("<tr><td >" + order.getId() + "</td>");
			writer.println("<td >" + sdf.format(order.getOrderDate()) + "</td>");

			writer.println("<td ><a href=\"?orderId=" + order.getId() + "\">" + order.getProduct().getTitle() + "</a></td> ");
			writer.println("<td > " + order.getProduct().getDescription() + "</td> ");
			writer.println("<td > " + order.getProduct().getType() + "</td> ");
			
			if(custInfo != null && !custInfo.isEmpty()){
				writer.println("<td > " + order.getCustomer().getLastName()  + ", " + order.getCustomer().getFirstName() + "</td> ");
				writer.println("<td > " + order.getCustomer().getPhone() + "</td> ");
				writer.println("<td > " + order.getCustomer().getEmail()+ "</td> ");
			}
			writer.println(" </tr>");
		}
		writer.println("</tbody></table>");
	}

	private void showOrder(PrintWriter writer, String orderId, String custInfo) {
	    SimpleDateFormat sdf = new SimpleDateFormat();
		Order order = orderService.getOrder(orderId);
		if (order != null) {
			
			writer.println("<h1 class='header center orange-text'>Order Information</h1>");
			writer.println("<div class='container'>");
			writer.println("<div class='section'><div class='row'>");
			
			writer.println("Order Id : " + order.getId());
			writer.println("<br>");
			if (order.getOrderDate() != null) {
			    writer.println("Order date: " + sdf.format(order.getOrderDate()));
			}
			
			writer.println("<br>");
			writer.println("<br>");
			writer.println("<b>Product Details :</b> <br>" );
			writer.println("Name : "+ order.getProduct().getTitle());
			writer.println("<br>");
			writer.println("Description : "+ order.getProduct().getDescription() );
			writer.println("<br>");
			writer.println(order.getProduct().getLongDesc() );
			writer.println("<br>");
			writer.println("Type : "+ order.getProduct().getType());
			writer.println("<br>");
			writer.println("Brand : "+ order.getProduct().getBrand());
			
			if(custInfo != null && !custInfo.isEmpty()){
				writer.println("<br>");
				writer.println("<br>");
				writer.println("<b>Customer Details :</b> <br>" );
				writer.println("Customer Name : "+ order.getCustomer().getLastName() + " " + order.getCustomer().getFirstName() );
				writer.println("<br>");
				writer.println("Mobile Number : "+ order.getCustomer().getPhone() );
			}
			
			writer.println("</div></div>");
			writer.println("</div>");
		} else {
			writer.println("Order with id " + orderId + " not found");
		}
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	private String getCustomerMicroService(){
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		OutputStreamWriter outputStreamWriter = null;
		String responseMessageFromServer = null;
		String responseXML = null;
		try {
			url = new URL("http://localhost:8181/cxf/cust/customer");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(HttpMethod.GET);
			httpURLConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			httpURLConnection.setDoOutput(true);
			if (httpURLConnection.getResponseCode() == 200) {
				responseMessageFromServer = httpURLConnection.getResponseMessage();
				responseXML = getResponseXML(httpURLConnection);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpURLConnection.disconnect();
		}
		return responseXML;
	}
	
	private String getResponseXML(HttpURLConnection httpURLConnection) throws IOException {
		//System.out.println("--------------- getResponseXML() calling ----------");
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		String readSingleLine = null;

		try {
			// read the response stream AND buffer the result into a
			// StringBuffer
			inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);

			// reading the XML response content line BY line
			while ((readSingleLine = bufferedReader.readLine()) != null) {
				stringBuffer.append(readSingleLine);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// finally close all operations
			bufferedReader.close();
			httpURLConnection.disconnect();
		}
		return stringBuffer.toString();
	}
	
	private void constructPage(PrintWriter writer, String orderId, String custInfo) {
		String line = "";
		String lineBtm = "";
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add( path + "header.jsp");
		pages.add( path + "navigation.jsp");
		pages.add("content");
		pages.add( path + "footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String pgName = (String) iterator.next();
			try {
				if(!("content".equals(pgName))){
					bufRedTop = new BufferedReader(new FileReader(pgName));
					while( (line = bufRedTop.readLine()) != null ){
						writer.println( line.toString() );
					}
				}else{
					if (orderId != null && orderId.length() > 0) {
						showOrder(writer, orderId, custInfo);
					} else {
						showOrderList(writer, custInfo);
					}
				}
			} catch (IOException e ) {
				System.out.println("*** ERROR: Reading Order CSV File Issue");
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
