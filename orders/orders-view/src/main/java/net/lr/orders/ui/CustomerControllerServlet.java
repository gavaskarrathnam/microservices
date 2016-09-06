package net.lr.orders.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import net.lr.orders.model.Task;
import net.lr.orders.model.TaskService;

import java.io.File;
import java.io.IOException;
 



public class CustomerControllerServlet extends HttpServlet {
	// TaskService productService;

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		String customerId = req.getParameter("customerId");
		PrintWriter writer = resp.getWriter();
		if (customerId != null && customerId.length() > 0) {
			// showTask(writer, customerId);
		} else {
			showCustomerList(writer);
		}

		// String uri = "http://localhost:8181/cxf/prod/products";

	}

	private void showCustomerList(PrintWriter writer) {
		writer.println("<html><body>");

		writer.println("<h1>Customer Details</h1>");
		writer.println("<table border='1'><tr><td >");

		/*
		 * WebClient client =
		 * WebClient.create("http://localhost:8181/cxf/cust"); client.path(
		 * "/customer" ); client.accept("text/json"); CustomerList custLst =
		 * client.get(CustomerList.class); writer.println(" Customer List : " +
		 * custLst);
		 */

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

			// writer.println("Response code: " +
			// httpURLConnection.getResponseCode());

			if (httpURLConnection.getResponseCode() == 200) {

				responseMessageFromServer = httpURLConnection.getResponseMessage();
				// System.out.println("ResponseMessageFromServer: " +
				// responseMessageFromServer);
				responseXML = getResponseXML(httpURLConnection);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpURLConnection.disconnect();
		}
		
		/*CustomerList custList = new CustomerList();
		ObjectMapper mapper = new ObjectMapper();
		
	      try
	      {
	    	  custList =  mapper.readValue(new File("E:/workdesk/shoppingcart/cart-view/src/main/java/net/lr/shoppingcart/ui/customer.json"), CustomerList.class);
	      } catch (JsonGenerationException e)
	      {
	         e.printStackTrace();
	      } catch (JsonMappingException e)
	      {
	         e.printStackTrace();
	      } catch (IOException e)
	      {
	         e.printStackTrace();
	      }
	      writer.println(custList.getCustomer().size());*/
		
		
		
		
		
		

		writer.println(responseXML);

		writer.println("</td> </tr></table>");

		writer.println("</body></html>");
	}

	private static String getResponseXML(HttpURLConnection httpURLConnection) throws IOException {

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

	/*
	 * private void showTask(PrintWriter writer, String taskId) {
	 * SimpleDateFormat sdf = new SimpleDateFormat(); Task task =
	 * productService.getTask(taskId); if (task != null) { writer.println(
	 * "<h1>Task " + task.getTitle() + " </h1>"); if (task.getDueDate() != null)
	 * { writer.println("Due date: " + sdf.format(task.getDueDate()) + "<br/>");
	 * } writer.println(task.getDescription()); } else { writer.println(
	 * "Task with id " + taskId + " not found"); }
	 * 
	 * }
	 * 
	 * public void setProductService(TaskService productService) {
	 * this.productService = productService; }
	 */

}
