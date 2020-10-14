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
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.CreateFile;
import com.example.demo.entities.category;
import com.example.demo.entities.productdetail;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductDetailRepository;

@Controller
public class ProductController {
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private ProductDetailRepository productdetailRepo;
	
	@GetMapping("/editproduct/{idcat}")
	public String editproduct(@PathVariable("idcat") String idcat
			,Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		cat = categoryRepo.getOne(idcat);
		List<productdetail> products = new ArrayList<productdetail>();
		products = productdetailRepo.getByCategory(cat.getNameCat());
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "editproduct";
	}
	
	@GetMapping("/addoneproduct/{idcat}")
	public String addoneproduct(@PathVariable("idcat") String idcat
			,Model model) {
		category cat = new category();
		cat = categoryRepo.getOne(idcat);
		productdetail product = new productdetail();
		product.setCategory(cat.getNameCat());
		model.addAttribute("product", product);
		return "addoneproduct";
	}
	
	@PostMapping("/saveproduct")
	public String saveProduct(@ModelAttribute productdetail product
			, BindingResult errors
			, Model model
			,@RequestParam("have") String size
			,@RequestParam(name = "numS", defaultValue = "0") Integer numS
			,@RequestParam(name = "numM", defaultValue = "0") Integer numM
			,@RequestParam(name = "numL", defaultValue = "0") Integer numL
			,@RequestParam(name = "numXL", defaultValue = "0") Integer numXL
			,@RequestParam(name = "num", defaultValue = "0") Integer num
			,@RequestParam("imageFile") MultipartFile file) throws IOException{
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			product.setPhotoProduct(image);
		}
		if(size.equalsIgnoreCase("y")) {
			product.setS(numS);
			product.setM(numM);
			product.setL(numL);
			product.setXl(numXL);
			productdetailRepo.save(product);
		}else if(size.equalsIgnoreCase("n")){
			product.setNumberStock(num);
			productdetailRepo.save(product);
		}
		return "redirect:/editproduct/1";
	}
	
	@GetMapping("/addcategory")
	public String addcategory(Model model) {
		model.addAttribute("category", new category());
		return "addcategory";
	}
	
	@PostMapping("/savecat")
	public String saveCategory(@ModelAttribute category category
			, BindingResult errors
			, Model model
			,@RequestParam("imageFile") MultipartFile file) throws IOException {
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			category.setPhotoCat(image);
		}
		categoryRepo.save(category);
		return "redirect:/editproduct/1";
	}
	
	@PostMapping("/updatecat")
	public String updateMember(@ModelAttribute category category
			,@RequestParam("imageFile") MultipartFile file) throws IOException{
		System.out.println(category.getNameCat());
		category cat = new category();
		cat = categoryRepo.findById(category.getIdCategory()).get();
		cat.setDetailCat(category.getDetailCat());
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			cat.setPhotoCat(image);
		}
		categoryRepo.save(cat);
		System.out.println("Category Update!!");
		return "redirect:/editproduct/1";
	}
	
}
