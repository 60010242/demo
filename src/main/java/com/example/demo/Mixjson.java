package com.example.demo;

import java.util.List;

public class Mixjson {
	private Userjson user;
	private List<Orderjson> orderlist;
	
	public Userjson getUser() {
		return user;
	}
	public void setUser(Userjson user) {
		this.user = user;
	}
	public List<Orderjson> getOrderlist() {
		return orderlist;
	}
	public void setOrderlist(List<Orderjson> orderlist) {
		this.orderlist = orderlist;
	}
	
}
