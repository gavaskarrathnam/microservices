package net.lr.orders.persistence.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.lr.orders.model.Customer;
import net.lr.orders.model.Order;
import net.lr.orders.model.OrderService;
import net.lr.orders.model.Product;


public class OrderServiceImpl implements OrderService{
	Map<String, Order> orderMap = new HashMap<String, Order>();
	
	public OrderServiceImpl() {
		orderMap = buildOrder();
	}
	
	public Order getOrder(String id) {
		return orderMap.get(id);
	}
	
	public void addOrder(Order order) {
		orderMap.put(order.getId(), order);
	}

	public Collection<Order> getOrder() {
		return new ArrayList<Order>(orderMap.values());
	}


	private Map<String, Order> buildOrder(){
		String line = "";
		BufferedReader bufRed = null;
		try {
			bufRed = new BufferedReader(new FileReader("E:/workdesk/orders/orders-persistence/src/main/java/net/lr/orders/persistence/impl/orders.csv"));
			while( (line = bufRed.readLine()) != null ){
				Order order = new Order();
				String[] orderCol = line.split(",");
				
				order.setId(orderCol[0]);

				Product product = new Product();
				product.setId(orderCol[1].trim());
				product.setTitle(orderCol[2].trim());
				product.setDescription(orderCol[3].trim());
				product.setDueDate(new Date());//new String(orderCol[4])
				product.setBrand(orderCol[5].trim());
				product.setType(orderCol[6].trim());
				product.setLongDesc(orderCol[7].trim());
				product.setImage(orderCol[8].trim());

				Customer cust = new Customer();
				cust.setId(Long.parseLong(orderCol[9].trim()));
				cust.setLastName(orderCol[10].trim());
				cust.setFirstName(orderCol[11].trim());
				cust.setEmail(orderCol[12].trim());
				cust.setPhone(new BigInteger(orderCol[13].trim()));
				
				order.setProduct(product);
				order.setCustomer(cust);
				
				order.setOrderDate(new Date());//new String(orderCol[14])

				orderMap.put(order.getId(), order);
			}
			
		} catch (IOException e ) {
			System.out.println("*** ERROR: Reading Order CSV File Issue");
		}finally {
			if (bufRed != null) {
				try {
					bufRed.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return orderMap;
	}
	

}
