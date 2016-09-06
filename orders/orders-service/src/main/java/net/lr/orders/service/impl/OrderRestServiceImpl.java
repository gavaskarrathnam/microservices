package net.lr.orders.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import net.lr.orders.model.Customer;
import net.lr.orders.model.Order;
import net.lr.orders.model.Product;
import net.lr.orders.service.OrderRestService;

public class OrderRestServiceImpl implements OrderRestService {

	private DataSource dataSource;
	
	Map<String, Order> orderMap = new HashMap<String, Order>();
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Order getOrder(String id) {
		return orderMap.get(id);
	}

	public void addOrder(Order order) {
		System.out.println("addOrder() CALLING *****" + order.getId());
		orderMap.put(order.getId(), order);
	}

	public Collection<Order> getOrder() {
		System.out.println(" ***** START :: OrderRestServiceImpl.getOrder() ******");
		
		// Build & fetch the Order data from CSV - For Testing purpose
		// orderMap = getOrderFromCSV();
		
		// Build & fetch the Order data from JDBC(DataSource) 
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append("select o.id,o.date,p.id, p.prodname,p.proddesc,p.prodtype,c.id,c.custfname, c.custlname,c.phone,c.email from orders o ");
		queryBuf.append("left join products p on o.productid = p.id ");
		queryBuf.append("left join customers c on o.customerid = c.id");
		try {
			Connection con = dataSource.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(queryBuf.toString());
			while (rs.next()) {
				// Retrieve by column name
				Order order = new Order();
				order.setId(rs.getString("o.id"));
				order.setOrderDate(rs.getDate("o.date"));

				Product product = new Product();
				product.setId(rs.getString("p.id"));
				product.setTitle(rs.getString("p.prodname"));
				product.setDescription(rs.getString("p.proddesc"));
				product.setType(rs.getString("p.prodtype"));
				order.setProduct(product);

				Customer customer = new Customer();
				customer.setId(rs.getInt("c.id"));
				customer.setFirstName(rs.getString("c.custfname"));
				customer.setLastName(rs.getString("c.custlname"));
				customer.setEmail(rs.getString("c.email"));
				customer.setPhone(rs.getBigDecimal("c.phone").toBigInteger());
				order.setCustomer(customer);

				orderMap.put(order.getId(), order);
			}
		}catch (SQLException sExp) {
			System.out.println("*** ERROR: SQL Exception occured - Connection JDBC (DataSource) Issue ");
			sExp.printStackTrace();
		}catch (Exception exp) {
			System.out.println("*** ERROR: Reading and Connection JDBC (DataSource) Issue " + exp.getMessage());			
			exp.printStackTrace();
		}
		System.out.println(" ***** END :: OrderRestServiceImpl.getOrder() ******");
		System.out.println("Map output :" + orderMap.size());
		
		return new ArrayList<Order>(orderMap.values());
	}

	/**
	 * Build & fetch the Order data from CSV - For Testing purpose
	 * 
	 * @return
	 */
	/*
	 * private Map<String, Order> getOrderFromCSV() { String line = "";
	 * BufferedReader bufRed = null; try { bufRed = new BufferedReader(new
	 * FileReader(
	 * "E:/workdesk/orders/orders-persistence/src/main/java/net/lr/orders/persistence/impl/orders.csv"
	 * )); while( (line = bufRed.readLine()) != null ){ Order order = new
	 * Order(); String[] orderCol = line.split(",");
	 * 
	 * order.setId(orderCol[0]);
	 * 
	 * Product product = new Product(); product.setId(orderCol[1].trim());
	 * product.setTitle(orderCol[2].trim());
	 * product.setDescription(orderCol[3].trim()); product.setDueDate(new
	 * Date());//new String(orderCol[4]) product.setBrand(orderCol[5].trim());
	 * product.setType(orderCol[6].trim());
	 * product.setLongDesc(orderCol[7].trim());
	 * product.setImage(orderCol[8].trim());
	 * 
	 * Customer cust = new Customer();
	 * cust.setId(Long.parseLong(orderCol[9].trim()));
	 * cust.setLastName(orderCol[10].trim());
	 * cust.setFirstName(orderCol[11].trim());
	 * cust.setEmail(orderCol[12].trim()); cust.setPhone(new
	 * BigInteger(orderCol[13].trim()));
	 * 
	 * order.setProduct(product); order.setCustomer(cust);
	 * 
	 * order.setOrderDate(new Date());//new String(orderCol[14])
	 * 
	 * orderMap.put(order.getId(), order); }
	 * 
	 * } catch (IOException e ) {
	 * System.out.println("*** ERROR: Reading Order CSV File Issue"); }finally {
	 * if (bufRed != null) { try { bufRed.close(); } catch (IOException e) {
	 * e.printStackTrace(); } } } return orderMap; }
	 */
}
