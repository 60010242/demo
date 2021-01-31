package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entities.category;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductDetailRepository;

@Controller
public class GeneralController {
	
	@GetMapping("/firstpage")
	public String showuser(@SessionAttribute("user") userprofile user, Model model) {
		String web = "";
		if(user.getType().equalsIgnoreCase("Customer")) {
			web = "firstpage";
		}else if(user.getType().equalsIgnoreCase("Seller")) {
			web = "redirect:/editproduct";//"redirect:/editproduct";
		}
		return web;
	}
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private ProductDetailRepository productdetailRepo;
	
	@GetMapping("/testpage")
	public String testpage(Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		String idcat = categoryRepo.getMinIdcategory();
		List<productdetail> products = new ArrayList<productdetail>();
		if(idcat != null) {
			cat = categoryRepo.getOne(idcat);
			products = productdetailRepo.getByCategory(cat.getNameCat());
		}else {
			cat = null;
		}
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "testpage";
	}
	
	@GetMapping("/editproductpages")
	public String categorytabs(Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		String idcat = categoryRepo.getMinIdcategory();
		List<productdetail> products = new ArrayList<productdetail>();
		if(idcat != null) {
			cat = categoryRepo.getOne(idcat);
			products = productdetailRepo.getByCategory(cat.getNameCat());
		}else {
			cat = null;
		}
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		System.out.print("cattyyyyyyyyyy" + cat.getNameCat() + idcat);
		return "categorytabs";
	}
	
	@GetMapping("/editproductpage")
	public ModelAndView editProductPage() {


        ModelAndView editproductpage = new ModelAndView("editproduct");

        return editproductpage;
    }
}
