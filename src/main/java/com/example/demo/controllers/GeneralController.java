package com.example.demo.controllers;

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
			web = "firstpage";
		}else if(user.getType().equalsIgnoreCase("Seller")) {
			web = "redirect:/editproduct";
		}
		return web;
	}
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {


        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
}
