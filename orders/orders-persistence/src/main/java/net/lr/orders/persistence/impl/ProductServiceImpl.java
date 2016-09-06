package net.lr.orders.persistence.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.lr.orders.model.Product;
import net.lr.orders.model.ProductService;


public class ProductServiceImpl implements ProductService{
	Map<String, Product> productMap = new HashMap<String, Product>();;
	
	public ProductServiceImpl() {
		productMap = buildProducts();
	}
	
	public Product getProduct(String id) {
		return productMap.get(id);
	}

	/*public void addProduct(Product product) {
		productMap.put(product.getId(), product);
	}
	 */
	public Collection<Product> getProducts() {
		// productMap.values is not serializable
		return new ArrayList<Product>(productMap.values());
	}

	private Map<String, Product> buildProducts(){
		String line = "";
		BufferedReader bufRed = null;
		try {
			bufRed = new BufferedReader(new FileReader("E:/workdesk/orders/orders-persistence/src/main/java/net/lr/orders/persistence/impl/products.csv"));
			while( (line = bufRed.readLine()) != null ){
				Product product = new Product();
				String[] prodCol = line.split(",");
				
				product.setId(prodCol[0]);
				product.setTitle(prodCol[1]);
				product.setDescription(prodCol[2]);
				product.setDueDate(new Date());
				product.setBrand(prodCol[4]);
				product.setType(prodCol[5]);
				product.setLongDesc(prodCol[6]);
				product.setImage(prodCol[7]);
				product.setIcon(prodCol[8]);
				productMap.put(product.getId(), product);
			}
			
		} catch (IOException e ) {
			System.out.println("*** ERROR: Reading O - Product CSV File Issue");
		}finally {
			if (bufRed != null) {
				try {
					bufRed.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return productMap;
	}

}
