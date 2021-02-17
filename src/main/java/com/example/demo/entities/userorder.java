package com.example.demo.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "userorder")

public class userorder {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idOrder;
	private Integer totalOrder;
	private String nameDelivery;
	private String track;
	private String status;
	private Integer totalWeight;
	private String userBank;
	private String sellerBank;
	private Integer lastNum;
	private Integer payTotal;
	private LocalDateTime payTime;
	private String photoPay;
	private LocalDateTime cratedOrder;
	private int idUser;
	private Integer idAddress;

	@OneToMany
	private Set<orderdetail> orderdetails = new HashSet<orderdetail>();
	
	@OneToMany
	private Set<payment> payments = new HashSet<payment>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idUser", referencedColumnName = "idUser",insertable = false, updatable = false)
	private userprofile userprofile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idAddress", referencedColumnName = "idAddress",insertable = false, updatable = false)
	private useraddress useraddress;
	
	public Set<orderdetail> getOrderdetails() {
		return orderdetails;
	}
	public void setOrderdetails(Set<orderdetail> orderdetails) {
		this.orderdetails = orderdetails;
		for(orderdetail orderdetail :orderdetails) {
			orderdetail.setUserorder(this);
		}
	}
	public Set<payment> getPayments() {
		return payments;
	}
	public void setPayments(Set<payment> payments) {
		this.payments = payments;
		for(payment payment :payments) {
			payment.setUserorder(this);
		}
	}
	public userprofile getUserprofile() {
		return userprofile;
	}
	public void setUserprofile(userprofile userprofile) {
		this.userprofile = userprofile;
		this.userprofile.getUserorders().add(this);
	}
	public useraddress getUseraddress() {
		return useraddress;
	}
	public void setUseraddress(useraddress useraddress) {
		this.useraddress = useraddress;
		this.useraddress.getUserorders().add(this);
	}
	public Integer getIdAddress() {
		return idAddress;
	}
	public void setIdAddress(Integer idAddress) {
		this.idAddress = idAddress;
	}
	
	public int getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(int idOrder) {
		this.idOrder = idOrder;
	}
	public int getTotalOrder() {
		return totalOrder;
	}
	public void setTotalOrder(int totalOrder) {
		this.totalOrder = totalOrder;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(int totalWeight) {
		this.totalWeight = totalWeight;
	}
	public LocalDateTime getCratedOrder() {
		return cratedOrder;
	}
	public void setCratedOrder(LocalDateTime cratedOrder) {
		this.cratedOrder = cratedOrder;
	}
	public String getUserBank() {
		return userBank;
	}
	public void setUserBank(String userBank) {
		this.userBank = userBank;
	}
	public String getSellerBank() {
		return sellerBank;
	}
	public void setSellerBank(String sellerBank) {
		this.sellerBank = sellerBank;
	}
	public int getLastNum() {
		return lastNum;
	}
	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}
	public int getPayTotal() {
		return payTotal;
	}
	public void setPayTotal(int payTotal) {
		this.payTotal = payTotal;
	}
	public LocalDateTime getPayTime() {
		return payTime;
	}
	public void setPayTime(LocalDateTime payTime) {
		this.payTime = payTime;
	}
	public String getPhotoPay() {
		return photoPay;
	}
	public void setPhotoPay(String photoPay) {
		this.photoPay = photoPay;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	public String getNameDelivery() {
		return nameDelivery;
	}
	public void setNameDelivery(String nameDelivery) {
		this.nameDelivery = nameDelivery;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cratedOrder == null) ? 0 : cratedOrder.hashCode());
		result = prime * result + idOrder;
		result = prime * result + idUser;
		result = prime * result + lastNum;
		result = prime * result + ((nameDelivery == null) ? 0 : nameDelivery.hashCode());
		result = prime * result + ((payTime == null) ? 0 : payTime.hashCode());
		result = prime * result + payTotal;
		result = prime * result + ((photoPay == null) ? 0 : photoPay.hashCode());
		result = prime * result + ((sellerBank == null) ? 0 : sellerBank.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + totalOrder;
		result = prime * result + totalWeight;
		result = prime * result + ((track == null) ? 0 : track.hashCode());
		result = prime * result + ((userBank == null) ? 0 : userBank.hashCode());
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
		userorder other = (userorder) obj;
		if (cratedOrder == null) {
			if (other.cratedOrder != null)
				return false;
		} else if (!cratedOrder.equals(other.cratedOrder))
			return false;
		if (idOrder != other.idOrder)
			return false;
		if (idUser != other.idUser)
			return false;
		if (lastNum != other.lastNum)
			return false;
		if (nameDelivery == null) {
			if (other.nameDelivery != null)
				return false;
		} else if (!nameDelivery.equals(other.nameDelivery))
			return false;
		if (payTime == null) {
			if (other.payTime != null)
				return false;
		} else if (!payTime.equals(other.payTime))
			return false;
		if (payTotal != other.payTotal)
			return false;
		if (photoPay == null) {
			if (other.photoPay != null)
				return false;
		} else if (!photoPay.equals(other.photoPay))
			return false;
		if (sellerBank == null) {
			if (other.sellerBank != null)
				return false;
		} else if (!sellerBank.equals(other.sellerBank))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (totalOrder != other.totalOrder)
			return false;
		if (totalWeight != other.totalWeight)
			return false;
		if (track == null) {
			if (other.track != null)
				return false;
		} else if (!track.equals(other.track))
			return false;
		if (userBank == null) {
			if (other.userBank != null)
				return false;
		} else if (!userBank.equals(other.userBank))
			return false;
		return true;
	}
	
}
