package com.hex.shopper.model;

import java.io.Serializable;

public class Cart implements Serializable {
	/**
	 * Only needed for ECF generic transport 
	 */
	private static final long serialVersionUID = 5117254163782139591L;

	private String id;
	private String cust_id;
	private String prod_id;
	private String prod_name;
	private String prod_desc;
	private String qty;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getProd_desc() {
		return prod_desc;
	}
	public void setProd_desc(String prod_desc) {
		this.prod_desc = prod_desc;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	
	

	
	
	
}
