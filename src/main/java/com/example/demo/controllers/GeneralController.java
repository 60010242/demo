package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entities.userprofile;

@Controller
public class GeneralController {
	
	@GetMapping("/firstpage")
	public String showuser(@SessionAttribute("user") userprofile user, Model model) {
		String web = "";
		if(user.getType().equalsIgnoreCase("Customer")) {
			web = "redirect:/buyproduct";
		}else if(user.getType().equalsIgnoreCase("Seller")) {
			web = "redirect:/editproduct";//"redirect:/editproduct";
		}
		return web;
	}
		
	@GetMapping("/testpage")
	public String testpage(Model model) {
		return "testpage";
	}
	
	@GetMapping("/buytrack")
	public ModelAndView buytrack() {

        ModelAndView buytrack = new ModelAndView("buytrack");

        return buytrack;
    }
	
/*	@GetMapping("/buytransfer")
	public ModelAndView buytransfer() {

        ModelAndView buytransfer = new ModelAndView("buytransfer");

        return buytransfer;
    }*/
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {

        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
	
	@GetMapping("/cart")
	public String cart(Model model) {
		return "cart";
	}
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	void sendEmail() {

		SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo("rixshiki@gmail.com"); //set customer mail
        msg.setSubject("Testing from Spring Boot"); //subject mail
        msg.setText("Hello World \nSpring Boot Email"); //text will send to customer

		javaMailSender.send(msg);
		
		System.out.println("Done");
    }
}
