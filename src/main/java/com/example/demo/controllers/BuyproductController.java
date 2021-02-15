package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.category;
import com.example.demo.entities.productdetail;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.UserOrderRepository;

@Controller
public class BuyproductController {
	@Autowired
	private UserOrderRepository userorderRepo;
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	
	@Autowired
	private ProductDetailRepository productRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private ProductDetailRepository productdetailRepo;
	
	@GetMapping("/buyproduct")
	public String buyproduct(Model model) {
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
		String id = "noid";
		model.addAttribute("id", id);
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "buyproduct";
	}
	
	@GetMapping("/buyproduct/{idcat}/{idorder}")
	public String buyproduct(@PathVariable("idcat") String idcat
			,@PathVariable("idorder") String idorder
			,Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		cat = categoryRepo.getOne(idcat);
		List<productdetail> products = new ArrayList<productdetail>();
		products = productdetailRepo.getByCategory(cat.getNameCat());
		model.addAttribute("id", idorder);
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
        return "buyproduct";
    }
	
	@PostMapping("/saveorder")
	public String saveorder(@RequestParam(name = "idorder") String idorder
			,@RequestParam(name = "idproduct") int idproduct
			,@RequestParam(name = "idcategory") int idcategory
			,@RequestParam(name = "size") String size
			,@RequestParam(name = "numberorder") int numberorder) {
		return "";
	}
	
	@GetMapping("/cart")
	public String cart(Model model) {
		return "cart";
	}
}
