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
import java.util.Collection;
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
import com.hex.shopper.model.Product;
import com.hex.shopper.model.ProductList;
import com.hex.shopper.model.ProductWrapper;
import com.hex.shopper.util.HostHelper;


public class ProductControllerServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1615764397126195121L;
	//Get Host Name & port
	HostHelper helper = new HostHelper();
    Map<String, String> hostMap = helper.getIPAddress();
    
	HttpSession session;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// session is retrieved before getting the writer
		session = req.getSession(true); 
		
		resp.setContentType("text/html");
		String productId = req.getParameter("productId");
		PrintWriter writer = resp.getWriter();
		constructPage(writer, productId);
	}

	
	private void showProductList(PrintWriter writer) {
						
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		ProductList prodLst = gson.fromJson(callRest(""), ProductList.class);
		
		writer.println("<h1 class='header center orange-text'>Products</h1>");
		if(prodLst != null && prodLst.getProduct() != null){
			writer.println("<div class='container'>");
			writer.println("<div class='section'><div class='row'>");
			
			Collection<Product> product = prodLst.getProduct();
			for (Product prod : product) {
				writer.println("<div class='col s12 m4'><div class='icon-block'>");	
				writer.println("<h2 class='center light-blue-text'><i class='material-icons'>" + prod.getIcon() + "</i></h2>");
				writer.println("<h5 class='center'>" + "<td ><a href=\"?productId=" + prod.getId() + "\">" + prod.getTitle() + "</h5>");
				writer.println("<p class='light'>" + prod.getDescription() + "</p>");
				writer.println("</div></div>");
			}
		
			writer.println("</div></div>");
			writer.println("</div>");
		}else{
			writer.println("Products not found !");
		}
	}

	private void showProduct(PrintWriter writer, String productId) {
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();	
		
		String productMSURL = callRest(productId);
		if(productMSURL != null && !productMSURL.isEmpty()){
			ProductWrapper prodWrp = gson.fromJson(productMSURL, ProductWrapper.class);
			Product prod = prodWrp.getProduct();
			
			if (prod != null) {
				writer.println("<h1 class='header center orange-text'>"+prod.getTitle()+"</h1><br><br>");
				
				writer.println("<div class='container'>");
				writer.println("<div class='section'><div class='row'>");
				
				writer.println("<div class='col s6'><img src='"+ prod.getImage() +"' style='width:350px;height:450px;'></div>");
				writer.println("<div class='col s6'>");
				writer.println("<table border='1'><tbody>");
				writer.println("<tr><td style='width:100px;'>Short Description: </td><td> "+ prod.getDescription() +"</td></tr>");
				writer.println("<tr><td>About the Product: </td><td> "+ prod.getLongDesc() +" <br> For more information: <a href='http://172.25.108.59:8943/HexceleratorStudio/'>Hexcelerator Studio</a></br></td></tr>");
				writer.println("<tr><td>Type : </td><td> "+ prod.getType() +"</td></tr>");
				writer.println("<tr><td>Brand: </td><td> "+ prod.getBrand() +"</td></tr>");
				writer.println("<tr><td>Date : </td><td> "+ sdf.format(prod.getDueDate()) +"</td></tr>");
				writer.println("</tbody></table>");
				writer.println("</div>");
				
				writer.println("</div></div>");
				writer.println("<div class='row right'>");
				addProduct(prod);
				writer.println("<a href=\"http://"+ hostMap.get("karaf.ipaddress")+":"+hostMap.get("karaf.port")+"/cartRestServices/\" id='download-button' class='btn-large waves-effect waves-light orange'>Add to Cart</a>");
				writer.println("</div><br><br>");
				
				writer.println("</div>");
				writer.println("<p>");
			} else {
				writer.println("Product with id " + prod + " not found");
			}
		}else{
			writer.println("Products not found !");
			System.out.println("Product Microservice URL not found " + productMSURL);
		}
	}
	
	private void addProduct(Product prod) {
		//START :: Cart data
		Cart cart = new Cart();
		cart.setCust_id("5");
		cart.setProd_id(prod.getId());
		cart.setProd_name(prod.getTitle());
		cart.setProd_desc(prod.getDescription());
		cart.setQty("1");
		//END :: Cart data
		session.setAttribute("cart", cart);
	}
	
	
	
	
	private void constructPage(PrintWriter writer, String productId) {
		String line = "";
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add( "header.jsp" );
		pages.add( "navigation.jsp" );
		pages.add( "content" );
		pages.add( "footer.jsp" );
		
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
					if(productId != null && productId.length() > 0)
						showProduct(writer, productId);
					else
						showProductList(writer);
				}
				
			} catch (IOException e ) {
				System.out.println("*** ERROR: Reading JSP File Issue in Product Controller service ****");
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

	private String callRest(String productId){
		//System.out.println("***** calling callRest(String productId) ****** ");
		String response = "";
		
		/*
		String custURI = "http://localhost:8181/cxf/prod/products";
		System.out.println("Product Node from Product Controller :" + custURI);
		*/
		
		ZkConnect zkConn = new ZkConnect();
		String discoveryURI = zkConn.getNodeDetail("/discovery/catalog");
		discoveryURI = discoveryURI.replace("localhost", hostMap.get("karaf.ipaddress") );
		
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		try {
			if(productId != null && !productId.isEmpty()){
				//START :: for run localhost uncomment below code 
				discoveryURI = discoveryURI + "/productid=" + productId;
				//END :: for run localhost uncomment below code

				//START :: for run Docker uncomment
				//discoveryURI = discoveryURI.substring(0, (discoveryURI.length() - 1)) + "/" + productId;
				//START :: for run Docker uncomment
			}

			System.out.println("Product Node from Product Controller discoveryURI :" + discoveryURI + " Product ID :" + productId) ;
			
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
			System.out.println(" Zookeeper connection ISSUE :" + ex.getMessage());
			System.out.println(ex.getStackTrace());
		} finally {
			httpURLConnection.disconnect();
		}
		System.out.println("----> Response :" + response);
		return response;
	}
	
	private String getResponseXML(HttpURLConnection httpURLConnection) throws IOException {

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
