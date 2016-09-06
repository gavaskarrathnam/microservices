package com.hex.shopper.view;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hex.shopper.descovery.ZkConnect;
import com.hex.shopper.model.Order;
import com.hex.shopper.model.OrderList;
import com.hex.shopper.model.OrderWrapper;
import com.hex.shopper.util.HostHelper;

/**
 * 
 * @author 19208
 *
 */
public class OrderControllerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9064334976413201513L;
	
	//Get Host Name & port
	HostHelper helper = new HostHelper();
	Map<String, String> hostMap = helper.getIPAddress();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		String orderId = req.getParameter("orderId");
		String custInfo = getCustomerMicroService();
		PrintWriter writer = resp.getWriter();
		constructPage(writer, orderId, custInfo);
	}

	private void constructPage(PrintWriter writer, String orderId, String custInfo) {
		String line = "";
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add( "header.jsp");
		pages.add( "navigation.jsp");
		pages.add( "content");
		pages.add( "footer.jsp");

		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String pgName = (String) iterator.next();
			try {
				if(!("content".equals(pgName))){
					bufRedTop = new BufferedReader(new FileReader(helper.getRoot() + pgName));
					while( (line = bufRedTop.readLine()) != null ){
						
						line = line.toString().replace("localhost", hostMap.get("karaf.ipaddress"));
						line = line.replace("8181", hostMap.get("karaf.port"));						
						
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
	
	private void showOrderList(PrintWriter writer, String custInfo) {
		writer.println("<h1 class='header center orange-text'>Order Details</h1>");
		writer.println("<table class='bordered'>");
		writer.println("<tr><thead><td data-field='orderId'> Order Id </td><td data-field='orderDate'> Order Date </td> <td data-field='prodName'>Product Name</td> <td data-field='prodDesc'> Description </td> <td data-field='prodType'> Product Type </td> ");
		if(custInfo != null && !custInfo.isEmpty()){
			writer.println("<td data-field='custName'> Customer Name </td> <td data-field='custPhone'> Customer Phone# </td> <td data-field='custEmail'> Customer E-mail </td>");
		}
		writer.println("</tr><thead><tbody>");
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();	
		OrderList orderList = gson.fromJson(callRest(""), OrderList.class);
		if(orderList != null && orderList.getOrders() != null){
			List<Order> orders = orderList.getOrders();
			
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
		}else{
			writer.println("<tr><td > Order Micro service not found !</td></tr>");
		}
		writer.println("</tbody></table>");
	}

	private void showOrder(PrintWriter writer, String orderId, String custInfo) {
	    SimpleDateFormat sdf = new SimpleDateFormat();
	    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();	
	    OrderWrapper orderWpr = gson.fromJson(callRest(orderId), OrderWrapper.class);
	    if(orderWpr.getOrder() != null){
			Order order = orderWpr.getOrder();
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
		
	    }else{
	    	writer.println("Order with id " + orderId + " not found");
	    }
	}

	
	private String getCustomerMicroService(){
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		String responseXML = null;
		try {
			url = new URL("http://" + hostMap.get("karaf.ipaddress")+":"+hostMap.get("karaf.port")+"/cxf/cust/customer");
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(HttpMethod.GET);
			httpURLConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			httpURLConnection.setDoOutput(true);
			if (httpURLConnection.getResponseCode() == 200) {				
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
	
	private String callRest(String orderId){
		String response = null;
		
		ZkConnect zkConn = new ZkConnect();
		String discoveryURI = zkConn.getNodeDetail("/discovery/order");
		//System.out.println("Order Node from discoveryURI :" + discoveryURI);
		discoveryURI = discoveryURI.replace("localhost", hostMap.get("karaf.ipaddress"));
		
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		try {
			if(orderId != null && !orderId.isEmpty()){						
				//START :: for run localhost uncomment below code 
				discoveryURI = discoveryURI + "/orderid=" + orderId;
				//END :: localhost uncomment

				//START :: for run Docker uncomment
				//discoveryURI = discoveryURI.substring(0, (discoveryURI.length() - 1)) + "/" +orderId;
				//START :: for run Docker uncomment
			}
			
			System.out.println("Rest End-Point URL is : " + discoveryURI);
			
			url = new URL(discoveryURI);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(HttpMethod.GET);
			httpURLConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			httpURLConnection.setDoOutput(true);

			if (httpURLConnection.getResponseCode() == 200) {
				response = getResponseXML(httpURLConnection);
				// System.out.println("----> Response :" + response);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpURLConnection.disconnect();
		}
		return response;
	}
}