package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

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

import com.example.demo.entities.userprofile;
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
		return "login";
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
				model.addAttribute("message", message);
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
			,@RequestParam(name = "email") String email) {
		String message = "Check your E-mail to set a new password.";
		boolean pass = true;
		userprofile user = new userprofile();
		if(userprofileRepo.findOneByEmail(email)!=null) {
			user = userprofileRepo.findOneByEmail(email);
			//ส่งเมลส่งลิงค์ ...../setpassword/user.getIdUser()
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
	public String savenewpw(Model model
			,@RequestParam(name = "pw") String pw
			,@RequestParam(name = "iduser") Integer iduser) {
		userprofile user = new userprofile();
		user = userprofileRepo.findById(iduser).get();
		user.setPassword(pw);
		userprofileRepo.save(user);
		String message = "A new password has been set.";
		model.addAttribute("iduser", iduser);
		model.addAttribute("message", message);
		return "newpassword";
	}
}
