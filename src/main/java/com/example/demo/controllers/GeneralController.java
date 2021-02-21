package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {

        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
	
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	@Autowired
	private ProductDetailRepository productRepo;
	
	@GetMapping("/testpage/{msgtype}/{idorder}")
	public String testpage(@PathVariable("msgtype") Integer msg_type,
			@PathVariable("idorder") Integer idorder,
			HttpServletRequest request,
			Model model) {
		
		/* msg_type
		 * 0 = ผู้ขาย มีรายการคำสั่งซื้อใหม่, ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 1 = ผู้ขาย ทำการยกเลิกสินค้า, ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 2 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 3 = ผู้ซื้อ ถูกยกเลิกสินค้า*/
		
		String base_url = "http://"+request.getLocalName()+":7070";
		String target_mail = "rixshiki@gmail.com";
		String msg_subject, msg_body;
				
		msg_subject = mailText(msg_type ,idorder, base_url)[0];
		msg_body = mailText(msg_type ,idorder, base_url)[1];
		
		
		// set SMTP seller to send G-mail
		System.setProperty("spring.mail.host", "smtp.gmail.com");
		System.setProperty("spring.mail.port", "587");
		System.setProperty("spring.mail.username", "rixshiki@gmail.com");
		System.setProperty("spring.mail.password", "fmrftkyefqyonglu");
		
		System.out.println(msg_subject + "\n\n" +msg_body);
		
		model.addAttribute("msg_subject", msg_subject);
		model.addAttribute("msg_body", msg_body);
		
		//sendEmail(target_mail, subject[i], body_text_list[i]);
		
		return "testpage";
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
	
	
	// Message Send
	String[] mailText(Integer msg_type, Integer idorder, String base_url) {
		
		/* msg_type
		 * 0 = ผู้ขาย มีรายการคำสั่งซื้อใหม่, ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 1 = ผู้ขาย ทำการยกเลิกสินค้า, ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 2 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 3 = ผู้ซื้อ ถูกยกเลิกสินค้า*/
		
		String[] subject_list = {
				//seller
				"ท่านได้รับคำสั่งซื้อใหม่ ออเดอร์ #", "ท่านได้ทำการยกเลิกออเดอร์ #",
				//customer
				"รายการคำสั่งซื้อของท่าน ออเดอร์ #", "แจ้งเตือนถูกยกเลิกออเดอร์ #"};
		
		String[] header_list = {
				//seller
				"ออเดอร์ #", "ยกเลิกออเดอร์ #",
				//customer
				"ออเดอร์ #", "ผู้ขายได้ทำการยกเลิกออเดอร์ #"};
		
		String[] footer_list = {
				//seller
				"----\n\nราคารวมทั้งสิ้น ",
				"----\n\nต้องคืนเงินรวมทั้งสิ้น ",
				//customer
				"----\n\nราคารวมทั้งสิ้น ",
				"----\n\nท่านจะได้รับเงินคืนจำนวน "};
		
		String[] url_list = {
				base_url +"/checking",
				base_url +"/cancel/cancel",
				base_url +"/buytransfer",
				base_url
		};
				
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(idorder);
	    String myorder = "\n\nรายการสินค้า\n----\n";
	    Integer totalprice = 0;
	    
	    // order detail text in mail
	    for(orderdetail order : orderlist) {
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	if(order.getSize()!=null) {
	    		myorder += product.getNameProduct()
	    				+" ไซส์"+order.getSize().toUpperCase()
	    	    		+" x"+order.getNumber()
	    	    		+" ราคา "+order.getRealPrice()+" บาท";
	    		totalprice += order.getRealPrice();
	    	}else {
	    		myorder += product.getNameProduct()
	    	    		+" x"+order.getNumber()
	    	    		+" ราคา"+order.getRealPrice()+" บาท";
	    		totalprice += order.getRealPrice();
	    	}
	    	myorder += "\n";
	    }
	    
	    myorder += footer_list[msg_type] + totalprice +" บาท\n\n"+ url_list[msg_type];
	   	
		String[] datasend = {
				subject_list[msg_type] + idorder,
				header_list[msg_type] + idorder + myorder};

		return datasend;
	}
}
