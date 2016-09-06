package com.hex.shopper.view;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.hex.shopper.descovery.ZkConnect;
import com.hex.shopper.model.CustomerBI;
import com.hex.shopper.util.HostHelper;

public class CustomerControllerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;
	
	//Get Host Name & port
	HostHelper helper = new HostHelper();
    Map<String, String> hostMap = helper.getIPAddress();
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		String customerId = req.getParameter("customerId");
		PrintWriter writer = resp.getWriter();

		constructProductPage(writer, customerId);
	}

	private void constructProductPage(PrintWriter writer, String customerId) {
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
					if (customerId != null && customerId.length() > 0){
						showCustomer(writer, customerId);
					}else{
						showCustomerList(writer);
						// showCustomersFromService(writer);
					}
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

	private void showCustomerList(PrintWriter writer) {

		/*
		 * WebClient client = WebClient.create("http://localhost:8181/cxf/cust",
		 * Collections.singletonList(new JacksonJsonProvider()));
		 * client.path("/customer");
		 * client.accept(MediaType.APPLICATION_JSON_TYPE); CustomerList custLst
		 * = client.get(CustomerList.class);
		 */

		/*
		 * Client client = ClientBuilder.newBuilder().newClient(); WebTarget
		 * target = client.target("http://localhost:8181/cxf/cust/customer");
		 * Invocation.Builder builder = target.request();
		 * builder.accept(MediaType.APPLICATION_JSON_TYPE); Response response =
		 * builder.get(); System.out.println(" Testing 0 ---->" +
		 * response.getEntity());
		 * 
		 * CustomerList custLst = (CustomerList)response.getEntity();
		 * System.out.println(" Testing 1 ---->" + response.getEntity());
		 * System.out.println(" Testing 2 ---->" +
		 * builder.get(CustomerList.class));
		 */

		Gson gson = new Gson();

		//CustomerList custLst = gson.fromJson(callRest(""), CustomerList.class);
		//CustomerBI cust = gson.fromJson(callRest("1"), CustomerBI.class);

		CustomerBI[] cArray = gson.fromJson(callRest(""), CustomerBI[].class);

		writer.println("<h1 class='header center orange-text'>Customer Details</h1>");
		if (cArray != null && cArray.length > 0) {
			//Collection<Customer> customerList = custLst.getCustomer();

			writer.println("<table class='striped'>");
			writer.println("<tr><thead>");
			writer.println("<td data-field='custId'> Customer Id </td><td data-field='custFirstName'> First Name </td> "
					+ "<td data-field='custLastName'> Last Name </td>"
					+ "<td data-field='custPhone'> Phone# </td> <td data-field='custEmail'> E-mail </td>");
			writer.println("</tr><thead><tbody>");

			for(int i =0; i< cArray.length; i++){
				writer.println("<tr>");
				writer.println("<td ><a href=\"?customerId=" + cArray[i].getId()+ "\">" + cArray[i].getId() + "</a></td>");
				writer.println("<td > " + cArray[i].getLastname() + "</td> ");
				writer.println("<td > " + cArray[i].getFirstname() + "</td> ");
				writer.println("<td > " + cArray[i].getPhone() + "</td> ");
				writer.println("<td > " + cArray[i].getEmail() + "</td> ");
				writer.println(" </tr>");
			}
			writer.println("</tbody></table>");
		} else {
			writer.println("<h3 class='center red-text'>Customers Not found</h1>");
		}
		
		
		/*if (custLst != null) {
			writer.println("<table class='striped'>");
			writer.println("<tr><thead>");
			writer.println("<td data-field='custId'> Customer Id </td> <td data-field='custLastName'> Last Name </td><td data-field='custFirstName'> First Name </td>"
					+ "<td data-field='custPhone'> Phone# </td> <td data-field='custEmail'> E-mail </td>");
			writer.println("</tr><thead><tbody>");

			for ( custLst) {
				writer.println("<tr>");
				writer.println("<td ><a href=\"?customerId=" + cust.getId()
						+ "\">" + cust.getId() + "</a></td>");
				writer.println("<td > " + cust.getLastName() + "</td> ");
				writer.println("<td > " + cust.getFirstName() + "</td> ");
				writer.println("<td > " + cust.getPhone() + "</td> ");
				writer.println("<td > " + cust.getEmail() + "</td> ");
				writer.println(" </tr>");
			}
			writer.println("</tbody></table>");
		} else {
			writer.println("<h3 class='center red-text'>Customers Not found</h1>");
		}
		*/
	}

	/*
	private String showCustomersFromService(PrintWriter writer) {

		System.out.println("***** getCustomerFromService after change the URI *****");

		String output = null;
		
		System.setProperty("http.proxyHost", "hexproxy.hexaware.local"); 
		System.setProperty("http.proxyPort", "3128");
		
		try {

			URL url = new URL("http://customerserviceapi.azurewebsites.net/api/Customers");
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(HttpMethod.GET);
			conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			conn.setDoOutput(true);
			
			System.out.println("Call URL : " + url );
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			while ( (output = br.readLine() ) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}
	*/
	
	private String callRest(String customerId) {
		
		System.out.println("Call URL callRest after change the URI");
		
		String response = null;
		//String custURI = "http://localhost:8181/cxf/cust/customer";
		//String custURI = "http://customerserviceapi.azurewebsites.net/api/Customers";

		ZkConnect zkConn = new ZkConnect();
		String discoveryURI = zkConn.getNodeDetail("/discovery/customer");
		//System.out.println("Product Node from Product Controller discoveryURI :" + discoveryURI);		
		discoveryURI = discoveryURI.replace("localhost",hostMap.get("karaf.ipaddress"));
		
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		
		//START :: Un-comment while running under localhost
		/*
		System.setProperty("http.proxyHost", "hexproxy.hexaware.local"); 
		System.setProperty("http.proxyPort", "3128");
		*/
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

	private void showCustomer(PrintWriter writer, String customerId) {

		Gson gson = new Gson();
		//System.out.println("Response from callRest() : " + callRest(customerId));
		CustomerBI customer = gson.fromJson(callRest(customerId), CustomerBI.class);

		writer.println("<h1 class='header center orange-text'> Customer Info. </h1><br><br>");
		writer.println("<div class='container'>");
		writer.println("<div class='section'><div class='row'>");

		writer.println("<div class='col s6'>");
		writer.println("<table border='1'><tbody>");
		writer.println("<tr><td style='width:100px;'>Customer Id: </td><td> " + customer.getId() + "</td></tr>");
		writer.println("<tr><td>Customer Name: </td><td> " + customer.getLastname() + ", " + customer.getFirstname() + "</td></tr>");
		writer.println("<tr><td>Phone# : </td><td> " + customer.getPhone() + "</td></tr>");
		writer.println("<tr><td>E-mail: </td><td> " + customer.getEmail() + "</td></tr>");
		writer.println("</tbody></table>");
		writer.println("</div>");

		writer.println("</div></div>");
		writer.println("</div><br><br>");

		writer.println("</div>");
		writer.println("<p>");
	}
}
