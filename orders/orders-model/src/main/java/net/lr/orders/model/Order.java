package net.lr.orders.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import net.lr.orders.model.Customer;
import net.lr.orders.model.Product;

@XmlRootElement()
public class Order implements Serializable {
	/**
	 * Only needed for ECF generic transport 
	 */
	private static final long serialVersionUID = 5117254163782139591L;

	String id;
	Customer customer;
	Product product;
	Date orderDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	

	
	
}
