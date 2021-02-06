package com.example.demo.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(orderdetailid.class)
@Table(name = "orderdetail")

public class orderdetail {
	@Id
	private int idOrder;
	@Id
	private int noOrder;
	private int number;
	private int idProduct;
	private int realPrice;
	private String size;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idProduct",referencedColumnName = "idProduct", insertable = false, updatable = false)
	private productdetail productdetail;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idOrder",referencedColumnName = "idOrder", insertable = false, updatable = false)
	private userorder userorder;
	
	public userorder getUserorder() {
		return userorder;
	}
	public void setUserorder(userorder userorder) {
		this.userorder = userorder;
		this.userorder.getOrderdetails().add(this);
	}
	
	public productdetail getProductdetail() {
		return productdetail;
	}
	public void setProductdetail(productdetail productdetail) {
		this.productdetail = productdetail;
		this.productdetail.getOrderdetails().add(this);
	}
	
	public int getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(int idOrder) {
		this.idOrder = idOrder;
	}
	public int getNoOrder() {
		return noOrder;
	}
	public void setNoOrder(int noOrder) {
		this.noOrder = noOrder;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(int idProduct) {
		this.idProduct = idProduct;
	}
	public int getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(int realPrice) {
		this.realPrice = realPrice;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idOrder;
		result = prime * result + idProduct;
		result = prime * result + noOrder;
		result = prime * result + number;
		result = prime * result + realPrice;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		orderdetail other = (orderdetail) obj;
		if (idOrder != other.idOrder)
			return false;
		if (idProduct != other.idProduct)
			return false;
		if (noOrder != other.noOrder)
			return false;
		if (number != other.number)
			return false;
		if (realPrice != other.realPrice)
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}
	
}
