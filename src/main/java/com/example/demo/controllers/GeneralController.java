package com.example.demo.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userprofile;
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
	@GetMapping("/smtp")
	public String smtp() {
		return "smtptutorial";
	}
	@GetMapping("/smtpform")
	public String smtpform() {
		return "smtpform";
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
		 * 3 = ผู้ซื้อ ถูกยกเลิกสินค้า
		
		String base_url = "http://"+request.getLocalName()+":7070";
		String target_mail = "rixshiki@gmail.com";
		String msg_subject, msg_body;
				
		msg_subject = mailText(msg_type ,idorder, base_url)[0];
		msg_body = mailText(msg_type ,idorder, base_url)[1];
		
				
		System.out.println(msg_subject + "\n\n" +msg_body);
		
		model.addAttribute("msg_subject", msg_subject);
		model.addAttribute("msg_body", msg_body);*/
		
		//sendEmail(target_mail, msg_subject, msg_body);
		
		return "testpage";
	}
	
	public void toSendMail(Integer msg_type, Integer idorder, String target_mail) {
		/* msg_type
		 * 0 = ผู้ขาย มีรายการคำสั่งซื้อใหม่
		 * 1 = ผู้ขาย ทำการยกเลิกสินค้า
		 * 2 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 3 = ผู้ซื้อ ถูกยกเลิกสินค้า*/
		
		String base_url = "http://xxx:7070";
		String msg_subject, msg_body;
		
		msg_subject = mailText(msg_type ,idorder, base_url)[0];
		msg_body = mailText(msg_type ,idorder, base_url)[1];
		
		System.out.println(msg_subject +"\n\n"+ msg_body);
		
		//sendEmail(target_mail, msg_subject, msg_body);
	}
	
	
	void sendEmail(String target_mail, String subject, String body_text) {
		
		// seller session ..will use from database
		String username = "rixshiki@gmail.com",
				password = "fmrftkyefqyonglu";
		
		Properties props = new Properties();
		
		// set SMTP seller to send G-mail
		props.put("mail.host", "smtp.gmail.com");
		props.put("mail.port", "587");
		props.put("mail.username", username);
		props.put("mail.password", password);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.timeout", "5000");
		props.put("mail.smtp.writetimeout", "5000");
		props.put("mail.smtp.connectiontimeout", "5000");
		props.put("mail.smtp.starttls.enable", "true");
		
		Session mailSession = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		
		mailSession.setDebug(true);
		
		// Send mail
		MimeMessage msg = new MimeMessage(mailSession);

        try {
        	// target mail
			msg.setRecipients(Message.RecipientType.TO, target_mail);
			msg.setSubject(subject); //subject mail
			msg.setContent(body_text, "text/html; charset=utf-8"); //text will send to customer

			Transport.send(msg);
			
			System.out.println("Success");
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unsuccess");
		}
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
				"ออเดอร์ #", "ทำการยกเลิกออเดอร์ #",
				//customer
				"ออเดอร์ #", "ผู้ขายได้ทำการยกเลิกออเดอร์ #"};
		
		String[] footer_list = {
				//seller
				"\n\nราคารวมทั้งสิ้น ",
				"\n\nต้องคืนเงินรวมทั้งสิ้น ",
				//customer
				"\n\nราคารวมทั้งสิ้น ",
				"\n\nท่านจะได้รับเงินคืนจำนวน "};
		
		String[] url_list = {
				base_url +"/checking",
				base_url +"/cancel/cancel",
				base_url +"/buytransfer",
				base_url
		};
		String css =
				"<style>"
				+ "table{ width: 500px; border: 1px solid gray; }"
				+ "table tr:first-child td{ background-color: gray; color: white; }"
				+ "table td:first-child{ width: 55%; }"
				+ "table td:NOT(first-child){ width: 15%; }"
				+ "table tr:last-child td{ background-color: lightgray; color: black; font-weight: bold; }"
				+ "</style>";
				
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(idorder);
		
		System.out.println(idorder+"list is "+orderlist);
		
	    String myorder = "\n<br/><table>"
	    		+ "<tr><td align='center' colspan='4'>รายการสินค้า</td></tr>";
	    Integer totalprice = 0;
	    
	    // order detail text in mail
	    for(orderdetail order : orderlist) {
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	if(order.getSize() != null) {
	    		myorder += "\n<tr><td>"+product.getNameProduct()
	    				+"</td>\n<td>ไซส์ "+order.getSize().toUpperCase()
	    	    		+"</td>\n<td>x"+order.getNumber()
	    	    		+"</td>\n<td>"+order.getRealPrice()+" บาท</td>";
	    		totalprice += order.getRealPrice();
	    	}else {
	    		myorder += "\n<tr><td colspan='2'>"+product.getNameProduct()
	    				+"</td>\n<td>x"+order.getNumber()
	    				+"</td>\n<td>"+order.getRealPrice()+" บาท</td>";
	    		totalprice += order.getRealPrice();
	    	}
	    	myorder += "\n";
	    }
	    
	    myorder += "\n<tr><td colspan='3'>รวม</td>\n<td>"+ totalprice +".-</td></tr></table>\n<br/>";
	    myorder += footer_list[msg_type] + totalprice +" บาท\n\n<br/><br/>"+ url_list[msg_type];
	   	
		String[] datasend = {
				subject_list[msg_type] + idorder,
				css+"<body>"+header_list[msg_type] + idorder + myorder+"</body>"};

		return datasend;
	}
}
