package net.lr.orders.service;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.lr.orders.model.Order;

@Path("/")
@Produces({MediaType.APPLICATION_JSON, "text/json"})
public interface OrderRestService {
	
	
	@GET
	@Path("orders/orderid={id}")	
	public Order getOrder(@PathParam("id") String id);
	
	@GET
	@Path("orders")	
	public Collection<Order> getOrder();
	
	@POST
	public void addOrder(Order order);
}
