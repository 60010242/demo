package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
}
