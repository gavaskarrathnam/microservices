package com.hex.shopper.model;

import java.math.BigInteger;

public class CustomerBI {
	private long Id;
	private String firstname;
    private String lastname;
    private String email;
    private BigInteger Phone;
    
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public BigInteger getPhone() {
		return Phone;
	}
	public void setPhone(BigInteger phone) {
		Phone = phone;
	}
	
	
}
