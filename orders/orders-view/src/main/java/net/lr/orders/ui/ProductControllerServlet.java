package net.lr.orders.ui;


import java.io.IOException;
import java.io.PrintWriter;
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
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import net.lr.orders.model.Product;
import net.lr.orders.model.ProductService;

public class ProductControllerServlet extends HttpServlet {
	ProductService productService;

	/**
	 * 
	 */
	private static final long serialVersionUID = 34992072289535683L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		String productId = req.getParameter("productId");
		PrintWriter writer = resp.getWriter();
		constructProductPage(writer, productId);
	}

	private void showProductList(PrintWriter writer) {
		writer.println("<h1 class='header center orange-text'>Products</h1>");
		writer.println("<div class='container'>");
		writer.println("<div class='section'><div class='row'>");
		
		Collection<Product> product = productService.getProducts();
		for (Product prod : product) {
			writer.println("<div class='col s12 m4'><div class='icon-block'>");	
			writer.println("<h2 class='center light-blue-text'><i class='material-icons'>" + prod.getIcon() + "</i></h2>");
			writer.println("<h5 class='center'>" + "<td ><a href=\"?productId=" + prod.getId() + "\">" + prod.getTitle() + "</h5>");
			writer.println("<p class='light'>" + prod.getDescription() + "</p>");
			writer.println("</div></div>");
		}
		writer.println("</div></div>");
		writer.println("</div>");
	}

	private void showProduct(PrintWriter writer, String productId) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		Product prod = productService.getProduct(productId);
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
			writer.println("<a href='http://localhost:8181/orders/' id='download-button' class='btn-large waves-effect waves-light orange'>Add to Cart</a>");
			writer.println("</div><br><br>");
			

			
			writer.println("</div>");
			writer.println("<p>");
		} else {
			writer.println("Product with id " + prod + " not found");
		}
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	
	
	private void constructProductPage(PrintWriter writer, String productId) {
		String line = "";
		String lineBtm = "";
		BufferedReader bufRedTop = null;
		
		List<String> pages = new ArrayList<String>();
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/header.jsp");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/navigation.jsp");
		pages.add("content");
		pages.add("E:/workdesk/shoppingcart/cart-view/src/main/webapp/footer.jsp");
		
		for (Iterator<String> iterator = pages.iterator(); iterator.hasNext();) {
			String pgName = (String) iterator.next();
			try {
				if(!("content".equals(pgName))){
					bufRedTop = new BufferedReader(new FileReader(pgName));
					while( (line = bufRedTop.readLine()) != null ){
						writer.println( line.toString() );
					}
				}else{
					if(productId != null && productId.length() > 0)
						showProduct(writer, productId);
					else
						showProductList(writer);
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
