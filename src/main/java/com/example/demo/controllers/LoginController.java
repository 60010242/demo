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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.smtp;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.SmtpRepository;
import com.example.demo.repositories.UserProfileRepository;

@Controller

@SessionAttributes("user")
public class LoginController {
	
	@Autowired
	private UserProfileRepository userprofileRepo;
	
	@ModelAttribute("user")
	public userprofile setUpUserForm() {
		return new userprofile();
	}
		
	@GetMapping("/")
	public String login(Model model) {
		return "test";
	}
		
	@PostMapping("/dologin")
	public String doLogin(@ModelAttribute("user") userprofile mem		//sessions
			, Model model) {
		List<userprofile> userList = new ArrayList<userprofile>();
		userList = userprofileRepo.findByemail(mem.getEmail());
		String web = "login";
		boolean message = false;
		// check user from db
		for(userprofile user : userList) {
			// set user data to sessions
			if(user.getEmail().equalsIgnoreCase(mem.getEmail()) && user.getPassword().equalsIgnoreCase(mem.getPassword())) {
					mem.setIdUser(user.getIdUser());
					mem.setBirthday(user.getBirthday());
					mem.setCoin(user.getCoin());
					mem.setGender(user.getGender());
					mem.setLine(user.getLine());
					mem.setName(user.getName());
					mem.setPhotoUser(user.getPhotoUser());
					mem.setTel(user.getTel());
					mem.setType(user.getType());
					web = "redirect:/firstpage";
			}else {
				message = true;
				model.addAttribute("message", "อีเมล์หรือรหัสผ่านของคุณไม่ถูกต้อง");
			}
		}
		return web;
	}
	
	@GetMapping("/logout")
	public String doLogout(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "redirect:/";
	}
	
	@GetMapping("/forgotpassword")
	public String forgotpassword(Model model) {
		
		return "forgotpassword";
	}
	
	@PostMapping("/sendforgot")
	public String sendforgot(Model model
			,@RequestParam(name = "email") String email
			, HttpServletRequest request) {
		String message = "Check your E-mail to set a new password.";
		boolean pass = true;
		userprofile user = new userprofile();
		if(userprofileRepo.findOneByEmail(email)!=null) {
			user = userprofileRepo.findOneByEmail(email);
			//ส่งเมลส่งลิงค์ ...../setpassword/user.getIdUser()
			toSendMail(5, user.getIdUser(), email, request.getLocalName());
		}else {
			pass = false;
			message = "Email is incorrect.";
		}
		model.addAttribute("pass", pass);
		model.addAttribute("message", message);
		return "forgotpassword";
	}
	
	@GetMapping("/setpassword/{iduser}")
	public String setpassword(Model model
			,@PathVariable("iduser") Integer iduser) {
		model.addAttribute("iduser", iduser);
		return "newpassword"; //หน้าแก้password
	}
	
	@PostMapping("/savenewpw")
	public ModelAndView savenewpw(Model model
			,RedirectAttributes modelfl
			,@RequestParam(name = "pw") String pw
			,@RequestParam(name = "iduser") Integer iduser) {
		userprofile user = new userprofile();
		user = userprofileRepo.findById(iduser).get();
		user.setPassword(pw);
		userprofileRepo.save(user);
		String message = "A new password has been set.";
		model.addAttribute("iduser", iduser);
		modelfl.addFlashAttribute("message", message);
		return new ModelAndView("redirect:/");
	}
	
/* ======================================== SMTP ======================================== */
	
	@Autowired
	private SmtpRepository smtpRepo;
	
	
	public String toSendMail(Integer msg_type, Integer idorder, String target_mail, String origin) {
		/* msg_type
		 * 0 = ผู้ขายลงทะเบียน Auto send mail
		 * 1 = ผู้ขาย มีรายการคำสั่งซื้อใหม่
		 * 2 = ผู้ขาย ทำการยกเลิกสินค้า
		 * 3 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 4 = ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 5 = ลืมรหัสผ่าน*/
		
		
		String base_url = "http://" +origin+ ":7070";
		String msg_subject, msg_body;
		
		msg_subject = mailText(msg_type ,idorder, base_url)[0];
		msg_body = mailText(msg_type ,idorder, base_url)[1];
		
		System.out.println(msg_subject +"\n\n"+ msg_body);
		
		return sendEmail(target_mail, msg_subject, msg_body);
	}
	
	
	String sendEmail(String target_mail, String subject, String body_text) {
		
		// seller session ..will use from database
		smtp smtp_have = new smtp();
		smtp_have = smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get();
		
		String resulttxt = "",
				username = smtp_have.getGmail(),
				password = smtp_have.getPasswordgen(); //fmrftkyefqyonglu
		
		
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
			
			resulttxt = "Success";
			System.out.println(resulttxt);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulttxt = "Fail";
			System.out.println(resulttxt);
		}
        
        return resulttxt;
	}
	
	
	// Message Send
	String[] mailText(Integer msg_type, Integer iduser, String base_url) {
		
		/* msg_type
		 * 0 = ผู้ขายลงทะเบียน Auto send mail
		 * 1 = ผู้ขาย มีรายการคำสั่งซื้อใหม่, ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 2 = ผู้ขาย ทำการยกเลิกสินค้า, ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 3 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 4 = ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 5 = ลืมรหัสผ่าน*/

		String css =
				"<style>"
				+ "table{ width: 500px; border: 1px solid gray; }"
				+ "table tr:first-child td{ background-color: gray; color: white; }"
				+ "table td:first-child{ width: 55%; }"
				+ "table td:NOT(first-child){ width: 15%; }"
				+ "table tr:last-child td{ background-color: lightgray; color: black; font-weight: bold; }"
				+ "</style>";
				
		
	    String[] datasend = { 
					"แก้ไขรหัสผ่านของท่าน", css+"<body>"+"ตั้งค่ารหัสผ่านใหม่"+"<br/><br/>"+base_url +"/setpassword/"+iduser+"</body>"};
		return datasend;
	}
}
