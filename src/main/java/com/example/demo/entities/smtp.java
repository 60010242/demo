package com.example.demo.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "smtp")

public class smtp {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idSmtp;
	private String gmail;
	private String passwordgen;
	private boolean usable;
	
	public int getIdSmtp() {
		return idSmtp;
	}
	public void setIdSmtp(int idSmtp) {
		this.idSmtp = idSmtp;
	}
	public String getGmail() {
		return gmail;
	}
	public void setGmail(String gmail) {
		this.gmail = gmail;
	}
	public String getPasswordgen() {
		return passwordgen;
	}
	public void setPasswordgen(String passwordgen) {
		this.passwordgen = passwordgen;
	}
	public boolean isUsable() {
		return usable;
	}
	public void setUsable(boolean usable) {
		this.usable = usable;
	}
	
}
