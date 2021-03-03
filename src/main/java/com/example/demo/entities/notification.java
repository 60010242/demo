package com.example.demo.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notification")
public class notification {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idNoti;
	private String subject;
	private String message;
	private int userread;
	private LocalDateTime createdNoti;
	private int idUser;
	private int idOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idUser", referencedColumnName = "idUser",insertable = false, updatable = false)
	private userprofile userprofile;
	public userprofile getUserprofile() {
		return userprofile;
	}
	public void setUserprofile(userprofile userprofile) {
		this.userprofile = userprofile;
		this.userprofile.getNotifications().add(this);
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idOrder",referencedColumnName = "idOrder", insertable = false, updatable = false)
	private userorder userorder;
	public userorder getUserorder() {
		return userorder;
	}
	public void setUserorder(userorder userorder) {
		this.userorder = userorder;
		this.userorder.getNotifications().add(this);
	}
	
	public Integer getIdNoti() {
		return idNoti;
	}
	public void setIdNoti(Integer idNoti) {
		this.idNoti = idNoti;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getCreatedNoti() {
		return createdNoti;
	}
	public void setCreatedNoti(LocalDateTime createdNoti) {
		this.createdNoti = createdNoti;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public int getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(int idOrder) {
		this.idOrder = idOrder;
	}
	public int getUserread() {
		return userread;
	}
	public void setUserread(int userread) {
		this.userread = userread;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdNoti == null) ? 0 : createdNoti.hashCode());
		result = prime * result + ((idNoti == null) ? 0 : idNoti.hashCode());
		result = prime * result + idOrder;
		result = prime * result + idUser;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + userread;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		notification other = (notification) obj;
		if (createdNoti == null) {
			if (other.createdNoti != null)
				return false;
		} else if (!createdNoti.equals(other.createdNoti))
			return false;
		if (idNoti == null) {
			if (other.idNoti != null)
				return false;
		} else if (!idNoti.equals(other.idNoti))
			return false;
		if (idOrder != other.idOrder)
			return false;
		if (idUser != other.idUser)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (userread != other.userread)
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}
	
}
