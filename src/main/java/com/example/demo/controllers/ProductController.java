package com.example.demo.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.example.demo.entities.notification;
import com.example.demo.entities.productdetail;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ProductDetailRepository;

@Controller
public class ProductController {
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private ProductDetailRepository productdetailRepo;
	
	@Autowired
	private NotificationRepository notiRepo;
	
	@GetMapping("/editproduct/{idcat}")
	public String editproduct(@PathVariable("idcat") String idcat
			,Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		cat = categoryRepo.getOne(idcat);
		List<productdetail> products = new ArrayList<productdetail>();
		products = productdetailRepo.getByCategory(cat.getNameCat());
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/editproduct/"+idcat);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "editproduct";
	}
	
	@GetMapping("/editproduct")
	public String editminproduct(Model model) {
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/editproduct");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		model.addAttribute("idcat", idcat);
		return "addoneproduct";
	}
	
	@PostMapping("/saveproduct")
	public String saveProduct(@ModelAttribute productdetail product
			, BindingResult errors
			, Model model
			,@RequestParam("have") String size
			,@RequestParam("idcat") String idcat
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
		product.setEnable(1);
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
		return "redirect:/editproduct/" + idcat;
	}
	
	@GetMapping("/editoneproduct/{idproduct}/{idcategory}")
	public String editoneproduct(@PathVariable("idproduct") Integer idproduct
			,@PathVariable("idcategory") Integer idcategory
			,Model model) {
		model.addAttribute("product", productdetailRepo.getOne(idproduct));
		model.addAttribute("idcategory", idcategory);
		return "editoneproduct";
	}

	@PostMapping("/updateproduct")
	public String updateproduct(@ModelAttribute productdetail product
			, BindingResult errors
			, Model model
			,@RequestParam("idproduct") Integer idproduct
			,@RequestParam("idcategory") Integer idcategory
			,@RequestParam(name = "numS", defaultValue = "0") Integer numS
			,@RequestParam(name = "numM", defaultValue = "0") Integer numM
			,@RequestParam(name = "numL", defaultValue = "0") Integer numL
			,@RequestParam(name = "numXL", defaultValue = "0") Integer numXL
			,@RequestParam(name = "num", defaultValue = "0") Integer num
			,@RequestParam("imageFile") MultipartFile file) throws IOException {
		productdetail pro = new productdetail();
		pro = productdetailRepo.findById(idproduct).get();
		pro.setCategory(product.getCategory());
		pro.setNameProduct(product.getNameProduct());
		pro.setPrice(product.getPrice());
		pro.setWeight(product.getWeight());
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			pro.setPhotoProduct(image);
		}
		if(pro.getNumberStock() == null) {
			pro.setS(numS);
			pro.setM(numM);
			pro.setL(numL);
			pro.setXl(numXL);
			productdetailRepo.save(pro);
		}else if(pro.getNumberStock() != null){
			pro.setNumberStock(num);
			productdetailRepo.save(pro);
		}
		System.out.println(product.getCategory());
		return "redirect:/editproduct/"+idcategory;
	}
	
	@GetMapping("/editshowproduct/{idproduct}/{idcat}")
	public String editshowproduct(@PathVariable("idcat") String idcat
			,@PathVariable("idproduct") Integer idproduct
			,Model model) {
		productdetail product = new productdetail();
		product = productdetailRepo.findById(idproduct).get();
		product.setEnable(0);
		productdetailRepo.save(product);
		return "redirect:/editproduct/" + idcat;
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
		return "redirect:/editproduct/" + category.getIdCategory();
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
		return "redirect:/editproduct/" + category.getIdCategory();
	}
	
	@GetMapping("/deletecategory/{idcategory}")
	public String deletecategory(@PathVariable("idcategory") int idcategory) {
		categoryRepo.deleteByIdCategory(idcategory);
		return "redirect:/firstpage";
	}
	
	@GetMapping("/deletephotocat/{idcategory}")
	public String deletephotocat(@PathVariable("idcategory") String idcategory) {
		category cat = new category();
		cat = categoryRepo.findById(idcategory).get();
		cat.setPhotoCat(null);
		categoryRepo.save(cat);
		return "redirect:/editproduct/"+idcategory;
	}
	
}
