package net.lr.orders.model;

import java.util.Collection;

public interface ProductService {
	public Product getProduct(String id);

	//public void addProduct(Product product);

	public Collection<Product> getProducts();
}
