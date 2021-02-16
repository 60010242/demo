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
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.demo.entities.category;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.orderdetailid;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
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
	public String saveorder(@SessionAttribute("user") userprofile user
			,@RequestParam(name = "idorder") String idorder
			,@RequestParam(name = "idproduct") int idproduct
			,@RequestParam(name = "idcategory") String idcategory
			,@RequestParam(name = "size", defaultValue = "null") String size
			,@RequestParam(name = "numberorder") int numberorder) {
		boolean save = false;
		productdetail product = new productdetail();
		product = productRepo.getByIdproduct(idproduct);
		if(idorder.equalsIgnoreCase("noid")) {						//ออเดอร์แรก
			userorder userorder = new userorder();
			userorder.setIdUser(user.getIdUser());
			userorder = userorderRepo.save(userorder);
			orderdetail order = new orderdetail();
			order.setIdOrder(userorder.getIdOrder());
			order.setNoOrder(1);
			order.setNumber(numberorder);
			order.setIdProduct(idproduct);
			order.setRealPrice(product.getPrice());
			if(product.getNumberStock()==null) {
				order.setSize(size);
			}
			orderdetailRepo.save(order);
			idorder = Integer.toString(userorder.getIdOrder());
			System.out.println("1");
		}else {
			List<orderdetail> orderlist = new ArrayList<orderdetail>();
			orderlist = orderdetailRepo.getByIdorder(Integer.parseInt(idorder));
			for(orderdetail orderdetail : orderlist) {								//กดเพิ่มซ้ำรายการเดิม
				if(idproduct == orderdetail.getIdProduct()) {
					if(product.getNumberStock()==null) {
						if(size.equalsIgnoreCase(orderdetail.getSize())) {
							orderdetail order = new orderdetail();
							orderdetailid orderid = new orderdetailid();
							orderid.setIdOrder(orderdetail.getIdOrder());
							orderid.setNoOrder(orderdetail.getNoOrder());
							order = orderdetailRepo.findById(orderid).get();
							int number = order.getNumber()+numberorder;
							order.setNumber(number);
							orderdetailRepo.save(order);
							save = true;
							System.out.println("2");
						}
					}else {
						orderdetail order = new orderdetail();
						orderdetailid orderid = new orderdetailid();
						orderid.setIdOrder(orderdetail.getIdOrder());
						orderid.setNoOrder(orderdetail.getNoOrder());
						order = orderdetailRepo.findById(orderid).get();
						int number = order.getNumber()+numberorder;
						order.setNumber(number);
						orderdetailRepo.save(order);
						save = true;
						System.out.println("2");
					}
				}
			}
			if(!save) {												//ออเดอร์ใหม่แต่มีidorderแล้ว
				int maxNo = orderdetailRepo.findMaxNoOrder(Integer.parseInt(idorder)); 
				orderdetail order = new orderdetail();
				order.setIdOrder(Integer.parseInt(idorder));
				order.setNoOrder(maxNo+1);
				order.setNumber(numberorder);
				order.setIdProduct(idproduct);
				order.setRealPrice(product.getPrice());
				if(product.getNumberStock()==null) {
					order.setSize(size);
				}
				orderdetailRepo.save(order);
				System.out.println("3");
			}
		}
		return "redirect:/buyproduct/"+ idcategory +"/"+ idorder;
	}
	
	@GetMapping("/cart")
	public String cart(Model model) {
		return "cart";
	}
}
