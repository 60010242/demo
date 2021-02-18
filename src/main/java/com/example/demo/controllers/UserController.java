package com.example.demo.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.example.demo.CreateFile;
import com.example.demo.entities.useraddress;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.UserAddressRepository;
import com.example.demo.repositories.UserProfileRepository;

@Controller
public class UserController {					// about user and user tables
	@Autowired
	private UserProfileRepository userprofileRepo;
	
	@Autowired
	private UserAddressRepository useraddressRepo;
	
	@GetMapping("/signup")
	public String greeting(Model model) {
		model.addAttribute("user", new userprofile());
		return "signup";
	}
	
	@PostMapping("/savemem")
	public String saveMember(@ModelAttribute userprofile user
			, BindingResult errors
			, Model model
			,@RequestParam("imageFile") MultipartFile file) throws IOException  {
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			user.setPhotoUser(image);
		}
		user.setType("Customer");				//Customer
		user.setCoin(0);
		userprofileRepo.save(user);
		model.addAttribute("message", "You have already signed.");
		model.addAttribute("user", new userprofile());
		return "signup";
	}
	
	@GetMapping("/myinfo")
	public String myinfo() {
		return "myinfo";
	}
	
	@GetMapping("/edit")
	public String editProfile(@SessionAttribute("user") userprofile user, Model model) {
		System.out.println("Edit id:"+ user.getIdUser());
		model.addAttribute("mem",userprofileRepo.getOne(user.getIdUser()));
		return "changeinfo";
	}
	
	@PostMapping("/updatemem")
	public String updateMember(@ModelAttribute userprofile member
			,@SessionAttribute("user") userprofile user
			,@RequestParam("imageFile") MultipartFile file) throws IOException {
		int id = user.getIdUser();
		System.out.print("Update id:");
		System.out.println(id);
		userprofile mem = new userprofile();
		mem = userprofileRepo.findById(id).get();
		mem.setName(member.getName());
		mem.setPassword(member.getPassword());
		mem.setTel(member.getTel());
		mem.setGender(member.getGender());
		
		user.setName(mem.getName());
		user.setPassword(mem.getPassword());
		user.setTel(mem.getTel());
		user.setGender(mem.getGender());
		
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			mem.setPhotoUser(image);
			user.setPhotoUser(mem.getPhotoUser());
		}
		userprofileRepo.save(mem);
		System.out.println("Update!!");
		return "redirect:/myinfo";
	}
	
	@GetMapping("/address")
	public String editAddress(@SessionAttribute("user") userprofile user,Model model) {
		List<useraddress> addresses = new ArrayList<useraddress>();
		addresses = useraddressRepo.getByIdUser(user.getIdUser());
		int count = useraddressRepo.countAddress(user.getIdUser());
		model.addAttribute("addresses", addresses);
		model.addAttribute("count", count);
		return "useraddress"; 
	}
	
	@PostMapping("/saveaddress")
	public String saveaddress(@RequestParam(name = "newaddress") String newaddress
			,@SessionAttribute("user") userprofile user) {
		useraddress address = new useraddress();
		address.setAddress(newaddress);
		address.setIdUser(user.getIdUser());
		useraddressRepo.save(address);
		return "redirect:/address";
	}
	
	@GetMapping("/deleteaddress/{idaddress}")
	public String deleteaddress(@PathVariable("idaddress") Integer idaddress) {
		useraddressRepo.deleteByIdAddress(idaddress);
		return "redirect:/address";
	}
}