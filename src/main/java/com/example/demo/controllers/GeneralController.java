package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Orderjson;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userprofile;
import com.example.demo.entities.yml;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;

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
	
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	@Autowired
	private ProductDetailRepository productRepo;
	
	@GetMapping("/testpage/{idorder}")
	public String testpage(@PathVariable("idorder") Integer idorder
			,Model model) {
		int i = 1;
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(idorder);
	    String myorder = "\n\nรายการ\n----\n";
	    Integer totalprice = 0;
	    
	    for(orderdetail order : orderlist) {
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	if(order.getSize()!=null) {
	    		myorder += product.getNameProduct()+" ไซส์"+
	    				order.getSize().toUpperCase()+" x"+
	    	    		order.getNumber()+" ราคา "+
	    	    		order.getRealPrice()+" บาท";
	    		totalprice += order.getRealPrice();
	    	}else {
	    		myorder += product.getNameProduct()+" x"+
	    	    		order.getNumber()+" ราคา"+
	    	    		order.getRealPrice()+" บาท";
	    		totalprice += order.getRealPrice();
	    	}
	    	myorder += "\n";
	    }
	    if(i==0) myorder += "----\n\nราคารวมทั้งสิ้น "+ totalprice +" บาท";
	    if(i==1) myorder += "----\n\nต้องคืนเงินรวมทั้งสิ้น "+ totalprice +" บาท";
	    
		orderlist = orderdetailRepo.getByIdorder(idorder);
		String customer_mail = "rixshiki@gmail.com";
		String[] subject_list = {
				"แจ้งเตือนคำสั่งซื้อใหม่ ออเดอร์ #"+ idorder,
				"แจ้งเตือนยกเลิกออเดอร์ #"+ idorder};
		String[] body_text_list = {
				"ออเดอร์ #"+ idorder + myorder,
				"ยกเลิกออเดอร์ #"+ idorder + myorder};
		
		//yaml_mail.setUsername("rixshiki@gmail.com");
		//yaml_mail.setPassword("fmrftkyefqyonglu");
		
		System.setProperty("spring.mail.host", "smtp.gmail.com");
		System.setProperty("spring.mail.port", "587");
		System.setProperty("spring.mail.username", "rixshiki@gmail.com");
		System.setProperty("spring.mail.password", "fmrftkyefqyonglu");
		
		System.out.println(body_text_list[i]);
		sendEmail(customer_mail, subject_list[i], body_text_list[i]);
		
		return "testpage";
	}
	
	
	
/*	@GetMapping("/buytrack")
	public ModelAndView buytrack() {

        ModelAndView buytrack = new ModelAndView("buytrack");

        return buytrack;
    }
	
	@GetMapping("/buytransfer")
	public String buytransfer() {

        return "buytransfer";
    }*/
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {

        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	void sendEmail(String customer_mail, String subject, String body_text) {

		SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(customer_mail); //set customer mail
        msg.setSubject(subject); //subject mail
        msg.setText(body_text); //text will send to customer

		javaMailSender.send(msg);
		
		System.out.println("Done");
    }
}
