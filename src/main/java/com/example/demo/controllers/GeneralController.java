package com.example.demo.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.CreateFile;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userprofile;
import com.example.demo.entities.smtp;
import com.example.demo.entities.useraddress;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.SmtpRepository;

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
	
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {

        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
	
	@Autowired
	private SmtpRepository smtpRepo;
	
	@GetMapping("/smtp")
	public String smtp() {
		return "smtptutorial";
	}
	
	@GetMapping("/smtpform")
	public String smtpForm(Model model) {
		if(smtpRepo.count() != 0) {
			smtp smtp_have = new smtp();
			smtp_have = smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get();
			model.addAttribute("smtp", smtp_have);
		}
		model.addAttribute("smtpcount", smtpRepo.count());
		return "smtpform";
	}
	
	@GetMapping("/savesmtp")
	public ModelAndView saveSmtp(@ModelAttribute smtp smtp
			, @RequestParam(name = "gmail") String gmail
			, @RequestParam(name = "passwordgen") String passwordgen
			, RedirectAttributes modelfl) throws IOException  {

		if(smtpRepo.count() == 0) {
			smtp smtp_dont = new smtp();
			smtp_dont.setGmail(gmail);
			smtp_dont.setPasswordgen(passwordgen);
			smtpRepo.save(smtp_dont);
			modelfl.addFlashAttribute("message", "You have already set up.");
		}
		else {
			smtp smtp_have = new smtp();
			smtp_have = smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get();
			smtp_have.setGmail(gmail);
			smtp_have.setPasswordgen(passwordgen);
			smtpRepo.save(smtp_have);
			modelfl.addFlashAttribute("message", "You have already set up.");
		}
		return new ModelAndView("redirect:/myinfo");
	}

	@GetMapping("/deletesmtp")
	public ModelAndView deleteSmtp(RedirectAttributes modelfl) {
		modelfl.addFlashAttribute("message", "You have already deleted.");
		smtpRepo.deleteById(smtpRepo.findAll().get(0).getIdSmtp());
		return new ModelAndView("redirect:/myinfo");
	}
	
	
	@GetMapping("/testpage")
	public String testpage() {
		
		return "testpage";
	}
	
}
