package net.lr.orders.model;

import java.util.Collection;

import net.lr.orders.model.Order;

public interface OrderService {
	public Order getOrder(String id);
	public Collection<Order> getOrder();
}
