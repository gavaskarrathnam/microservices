package com.hex.shopper.view;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hex.shopper.descovery.ZkConnect;
import com.hex.shopper.model.Cart;
import com.hex.shopper.util.HostHelper;

/**
 * 
 * @author 19208
 *
 */
public class CartControllerServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3770556063377232867L;

	//Get Host Name & port
	HostHelper helper = new HostHelper();
    Map<String, String> hostMap = helper.getIPAddress();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		if(req.getParameter("action") != null)
			System.out.println("Action Name :"+ req.getParameter("action"));
		
		if(req.getParameter("cartId") != null)
			System.out.println("CartId :"+ req.getParameter("cartId"));
		
		if(req.getParameter("action") != null && "delete".equals(req.getParameter("action"))){
			deleteProductFromCart(req.getParameter("cartId"));
		}else{
			HttpSession session = req.getSession(false);  
			if(session != null && session.getAttribute("cart") != null){
				Cart cart = (Cart) session.getAttribute("cart");
				if(cart != null && cart.getProd_id() != null){
					addProduct(cart);
				}
			}else{
				System.out.println("Cart attribute from Session is empty ");
			}
		}

		constructCartPage( writer );
	}

	/**
	 * Add Product
	 * @param cart
	 */
	private void addProduct(Cart cart){
		String pcfURL = getPCFRest("cartAdd");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		try {
			
			System.out.println("Add to cart POST url :" + pcfURL);
			
			URL url = new URL(pcfURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = gson.toJson(cart);

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
			int response = conn.getResponseCode();
			
			if(response != 200){
				System.out.println("ERROR :: Add cart get error !" + " Code :"+response);
				
			}
			
			/*Scanner scanner;
			String response;
			if (conn.getResponseCode() != 200) {
				scanner = new Scanner(conn.getErrorStream());
				response = "Error From Server \n\n";
			} else {
				scanner = new Scanner(conn.getInputStream());
				response = "Response From Server \n\n";
			}
			scanner.useDelimiter("\\Z");
			System.out.println(response + scanner.next());
			scanner.close();*/
			
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println("ERROR :: Add cart get error @ MalformedURLException !" + e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR :: Add cart get error in @ IOException !" + e.getMessage());
		}
	}
	
	
	private void deleteProductFromCart(String id){
		String pcfURL = getPCFRest("cartDel");
		
		try {			
			System.out.println("Delete to cart DELETE url :" + pcfURL);
			
			URL url = new URL(pcfURL + "/" + id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.getResponseCode();			
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println("ERROR :: Delete cart get error @ MalformedURLException !" + e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR :: Delete cart get error in @ IOException !" + e.getMessage());
		}
	}
	
	
	private void constructCartPage(PrintWriter writer) {
		String line = "";
		BufferedReader bufRedTop = null;

		List<String> pages = new ArrayList<String>();
		pages.add("header.jsp");
		pages.add("navigation.jsp");
		pages.add("content");
		pages.add("footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String pgName = (String) iterator.next();
			try {
				if (!("content".equals(pgName))) {
					bufRedTop = new BufferedReader(new FileReader(helper.getRoot() + pgName));
					while ((line = bufRedTop.readLine()) != null) {
						
						line = line.toString().replace("localhost", hostMap.get("karaf.ipaddress"));
						line = line.replace("8181", hostMap.get("karaf.port"));
						
						writer.println(line.toString());
					}
				} else {
					
					/*
					if (cartId != null && cartId.length() > 0){
						showCart(writer, cartId);
					}else{
						showCartList(writer);
					}
					*/
					
					showCartList(writer);
				}

			} catch (IOException e) {
				System.out.println("*** ERROR: Reading CSV File Issue");
			} finally {
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

	private void showCartList(PrintWriter writer) {
		Gson gson = new Gson();
		Cart[] cArray = gson.fromJson(callRest(""), Cart[].class);

		writer.println("<h1 class='header center orange-text'>Cart Details</h1>");
		if (cArray != null && cArray.length > 0) {

			writer.println("<table class='striped'>");
			writer.println("<tr><thead>");
			writer.println("<td data-field='custId'> Customer Id </td>");
			writer.println("<td data-field='prodId'> Product Id </td> ");
			writer.println("<td data-field='prodName'> Product Name </td>");
			writer.println("<td data-field='prodDesc'> Product Description</td>");
			writer.println("<td data-field='qty'> Quantity </td>");
			writer.println("<td>&nbsp;</td>");			
			writer.println("</tr><thead><tbody>");

			for(int i=0; i<cArray.length; i++){
				writer.println("<tr>");
				
				writer.println("<td > " + cArray[i].getCust_id() 	+ "</td> ");
				writer.println("<td > " + cArray[i].getProd_id() 	+ "</td> ");
				writer.println("<td > " + cArray[i].getProd_name() 	+ "</td> ");
				writer.println("<td > " + cArray[i].getProd_desc() 	+ "</td> ");
				writer.println("<td > " + cArray[i].getQty() 		+ "</td> ");
				
				writer.println("<td ><a href=\"http://"+hostMap.get("karaf.ipaddress")+":"+hostMap.get("karaf.port")+"/cartRestServices?action=delete&cartId=" + cArray[i].getId()+ "\">Delete</a></td>");
				
				writer.println(" </tr>");
			}
			writer.println("</tbody></table>");
		} else {
			writer.println("<h3 class='center red-text'>Cart is empty</h1>");
		}
		
	}

	private String callRest(String customerId) {
		System.out.println("Call URL callRest after change the URI");

		String response = null;
		String discoveryURI = getPCFRest("cart");
		System.out.println("Product Node from Product Controller discoveryURI :" + discoveryURI);		
		
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		
		//START :: Un-comment while running under localhost
		
		System.setProperty("http.proxyHost", "hexproxy.hexaware.local"); 
		System.setProperty("http.proxyPort", "3128");
		
		//END :: Un-comment while running under localhost
		
		try {
			if (customerId != null) {
				discoveryURI = discoveryURI + "/" + customerId;
			}

			System.out.println("Call URL is : " + discoveryURI + " Customer ID : "+ customerId);

			url = new URL(discoveryURI);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(HttpMethod.GET);
			httpURLConnection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			httpURLConnection.setDoOutput(true);

			System.out.println("Response code : " + httpURLConnection.getResponseCode());
			
			if (httpURLConnection.getResponseCode() == 200) {
				response = getResponseXML(httpURLConnection);
			}
		} catch (Exception ex) {
			System.out.println(" Zookeeper connection ISSUE from Customer servlet :" + ex.getMessage());
			System.out.println(ex.getStackTrace());
		} finally {
			httpURLConnection.disconnect();
		}
		System.out.println("----> Response :" + response);

		return response;
	}

	/**
	 * PCF call
	 * @return
	 */
	private String getPCFRest(String action){
		ZkConnect zkConn = new ZkConnect();
		return zkConn.getNodeDetail("/discovery/"+action);		
	}
	
	private String getResponseXML(HttpURLConnection httpURLConnection)
			throws IOException {

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

}