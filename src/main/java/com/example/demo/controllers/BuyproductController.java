package com.example.demo.controllers;

import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.demo.OrderId;
import com.example.demo.Productjson;
import com.example.demo.entities.category;
import com.example.demo.entities.delivery;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.orderdetailid;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.useraddress;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.DeliveryRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.UserAddressRepository;
import com.example.demo.repositories.UserOrderRepository;

@Controller
@SessionAttributes("orderid")
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
	
	@Autowired
	private UserAddressRepository useraddressRepo;
	
	@Autowired
	private DeliveryRepository deliveryRepo;
	
	@ModelAttribute("orderid")
	public OrderId setUporderid() {
		return new OrderId();
	}
	
	@GetMapping("/buyproduct")
	public String buyproduct(@ModelAttribute("orderid") OrderId Sorderid
			,Model model) {
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
		if(Sorderid.getOrderid() == null) {
			Sorderid.setOrderid("noid");
		}
		System.out.println(Sorderid.getOrderid());
		int numcart = 0;
		if(!(Sorderid.getOrderid().equalsIgnoreCase("noid"))) {
			numcart = orderdetailRepo.countNoOrderbyId(Integer.parseInt(Sorderid.getOrderid()));
		}
		model.addAttribute("numcart", numcart);
		model.addAttribute("id", Sorderid.getOrderid());
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "buyproduct";
	}
	
	@GetMapping("/buyproduct/{idorder}")
	public String buyproduct(@PathVariable("idorder") String idorder
			,@ModelAttribute("orderid") OrderId Sorderid
			,Model model) {
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
		int numcart = 0;
		if(!(Sorderid.getOrderid().equalsIgnoreCase("noid"))) {
			numcart = orderdetailRepo.countNoOrderbyId(Integer.parseInt(Sorderid.getOrderid()));
		}
		model.addAttribute("numcart", numcart);
		model.addAttribute("id", Sorderid.getOrderid());
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
		return "buyproduct";
	}
	
	@GetMapping("/buyproduct/{idcat}/{idorder}")
	public String buyproduct(@PathVariable("idcat") String idcat
			,@ModelAttribute("orderid") OrderId Sorderid
			,@PathVariable("idorder") String idorder
			,Model model) {
		List<category> catlist = new ArrayList<category>();
		catlist = categoryRepo.findAll();
		category cat = new category();
		cat = categoryRepo.getOne(idcat);
		List<productdetail> products = new ArrayList<productdetail>();
		products = productdetailRepo.getByCategory(cat.getNameCat());
		int numcart = 0;
		if(!(Sorderid.getOrderid().equalsIgnoreCase("noid"))) {
			numcart = orderdetailRepo.countNoOrderbyId(Integer.parseInt(Sorderid.getOrderid()));
		}
		model.addAttribute("numcart", numcart);
		model.addAttribute("id", Sorderid.getOrderid());
		model.addAttribute("products", products);
		model.addAttribute("cat", cat);
		model.addAttribute("catlist", catlist);
        return "buyproduct";
    }
	
	@PostMapping("/saveorder")
	public String saveorder(@SessionAttribute("user") userprofile user
			,@ModelAttribute("orderid") OrderId Sorderid
			,@RequestParam(name = "idorder") String idorder
			,@RequestParam(name = "idproduct") int idproduct
			,@RequestParam(name = "idcategory") String idcategory
			,@RequestParam(name = "size", defaultValue = "null") String size
			,@RequestParam(name = "numberorder") int numberorder) {
		boolean save = false;
		productdetail product = new productdetail();
		product = productRepo.getByIdproduct(idproduct);
		if(Sorderid.getOrderid().equalsIgnoreCase("noid")) {						//ออเดอร์แรก
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
			Sorderid.setOrderid(Integer.toString(userorder.getIdOrder()));
			System.out.println("1");
			System.out.println(Sorderid.getOrderid());
		}else {
			List<orderdetail> orderlist = new ArrayList<orderdetail>();
			orderlist = orderdetailRepo.getByIdorder(Integer.parseInt(Sorderid.getOrderid()));
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
							if(size.equalsIgnoreCase("s")) {
								if(number > product.getS()) {
									number = product.getS();
								}
							}else if(size.equalsIgnoreCase("m")) {
								if(number > product.getM()) {
									number = product.getM();
								}
							}else if(size.equalsIgnoreCase("l")) {
								if(number > product.getL()) {
									number = product.getL();
								}
							}else if(size.equalsIgnoreCase("xl")) {
								if(number > product.getXl()) {
									number = product.getXl();
								}
							}
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
						if(number > product.getNumberStock()) {
							number = product.getNumberStock();
						}
						order.setNumber(number);
						orderdetailRepo.save(order);
						save = true;
						System.out.println("2");
					}
				}
			}
			if(!save) {												//ออเดอร์ใหม่แต่มีidorderแล้ว
				int maxNo = 0; 
				if(orderdetailRepo.countNoOrderbyId(Integer.parseInt(Sorderid.getOrderid()))>0) {
					maxNo = orderdetailRepo.findMaxNoOrder(Integer.parseInt(Sorderid.getOrderid())); 
				}
				orderdetail order = new orderdetail();
				order.setIdOrder(Integer.parseInt(Sorderid.getOrderid()));
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
		return "redirect:/buyproduct/"+ idcategory +"/"+ Sorderid.getOrderid();
	}
	
	@GetMapping("/cart/{idorder}")
	public String cart(@PathVariable("idorder") String idorder
			,@ModelAttribute("orderid") OrderId Sorderid
			,Model model) {
		List<orderdetail> orders = new ArrayList<orderdetail>();
		int totalOrder = 0;
		if(!(Sorderid.getOrderid().equalsIgnoreCase("noid"))) {
			orders = orderdetailRepo.getByIdorder(Integer.parseInt(Sorderid.getOrderid()));
			for(orderdetail order : orders) {
				totalOrder = totalOrder + (order.getRealPrice()*order.getNumber());
			}
		}
		model.addAttribute("total", totalOrder);
		model.addAttribute("orders", orders);
		model.addAttribute("id", Sorderid.getOrderid());
		model.addAttribute("textno", "ไม่มีสินค้าในตะกร้า");
		return "cart";
	}
	
	@GetMapping("/minusorder/{idorder}/{noorder}")
	public String minusorder(@PathVariable("idorder") String idorder
			,@PathVariable("noorder") int noorder) {
		orderdetail order = new orderdetail();
		orderdetailid id = new orderdetailid();
		id.setIdOrder(Integer.parseInt(idorder));
		id.setNoOrder(noorder);
		order = orderdetailRepo.findById(id).get();
		order.setNumber(order.getNumber()-1);
		orderdetailRepo.save(order);
		return "redirect:/cart/"+idorder;
	}
	
	@GetMapping("/addorder/{idorder}/{noorder}")
	public String addorder(@PathVariable("idorder") String idorder
			,@PathVariable("noorder") int noorder) {
		orderdetail order = new orderdetail();
		orderdetailid id = new orderdetailid();
		id.setIdOrder(Integer.parseInt(idorder));
		id.setNoOrder(noorder);
		order = orderdetailRepo.findById(id).get();
		productdetail product = new productdetail();
		product = productRepo.getByIdproduct(order.getProductdetail().getIdProduct());
		int number = order.getNumber()+1;
		if(order.getSize()!=null) {
			if(order.getSize().equalsIgnoreCase("s")) {
				if(number > product.getS()) {
					number = product.getS();
				}
			}else if(order.getSize().equalsIgnoreCase("m")) {
				if(number > product.getM()) {
					number = product.getM();
				}
			}else if(order.getSize().equalsIgnoreCase("l")) {
				if(number > product.getL()) {
					number = product.getL();
				}
			}else if(order.getSize().equalsIgnoreCase("xl")) {
				if(number > product.getXl()) {
					number = product.getXl();
				}
			}
		}else {
			if(number > product.getNumberStock()) {
				number = product.getNumberStock();
			}
		}
		order.setNumber(number);
		orderdetailRepo.save(order);
		return "redirect:/cart/"+idorder;
	}
	
	@GetMapping("/deleteorder/{idorder}/{noorder}")
	public String deleteorder(@PathVariable("idorder") String idorder
			,@PathVariable("noorder") int noorder) {
		orderdetailRepo.deleteByIdAndNo(Integer.parseInt(idorder), noorder);
		return "redirect:/cart/"+idorder;
	}
	
	@GetMapping("/cartconfirm/{idorder}")
	public String cartconfirm(@PathVariable("idorder") String idorder
			,Model model) {
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(Integer.parseInt(idorder)).get();
		List<orderdetail> orders = new ArrayList<orderdetail>();
		orders = orderdetailRepo.getByIdorder(Integer.parseInt(idorder));
		int totalOrder = 0;
		int totalWeight = 0;
		int sendcost = 0;
		for(orderdetail order : orders) {
			totalOrder = totalOrder + (order.getRealPrice()*order.getNumber());
			totalWeight = totalWeight + (order.getProductdetail().getWeight()*order.getNumber());
		}
		if(userorder.getNameDelivery()==null) {
			userorder.setTotalOrder(totalOrder);
			userorder.setTotalWeight(totalWeight);
			userorder = userorderRepo.save(userorder);
		}else {
			List<delivery> delis = new ArrayList<delivery>();
			delis = deliveryRepo.getBynameDelivery(userorder.getNameDelivery());
			for(delivery deli : delis) {
				if(userorder.getTotalWeight()<=deli.getMaxWeight()) {
					userorder.setTotalOrder(totalOrder+deli.getPriceDelivery());
					userorder.setTotalWeight(totalWeight);
					userorder = userorderRepo.save(userorder);
					sendcost = deli.getPriceDelivery();
					break; 
				}
			}
		}
		model.addAttribute("sendcost", sendcost);
		model.addAttribute("id", idorder);
		model.addAttribute("userorder", userorder);
		model.addAttribute("orders", orders);
		return "cartconfirm";
	}
	
	@GetMapping("/cartaddress/{idorder}")
	public String cartaddress(@PathVariable("idorder") String idorder
			,@SessionAttribute("user") userprofile user
			,Model model) {
		List<useraddress> addresses = new ArrayList<useraddress>();
		addresses = useraddressRepo.getByIdUser(user.getIdUser());
		int count = useraddressRepo.countAddress(user.getIdUser());
		model.addAttribute("count", count);
		model.addAttribute("id", idorder);
		model.addAttribute("addresses", addresses);
		return "cartaddress";
	}
	
	@PostMapping("/savecartaddress/{idorder}")
	public String savecartaddress(@PathVariable("idorder") String idorder
			,@RequestParam(name = "address") String address) {
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(Integer.parseInt(idorder)).get();
		userorder.setUserAddress(address);
		userorderRepo.save(userorder);
		return "redirect:/cartconfirm/"+idorder;
	}
	
	@GetMapping("/cartdelivery/{idorder}")
	public String cartdelivery(@PathVariable("idorder") String idorder
			,Model model) {
		List<delivery> delilist = new ArrayList<delivery>();
		List<delivery> delis = new ArrayList<delivery>();
		List<String> namedeli = new ArrayList<String>();
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(Integer.parseInt(idorder)).get();
		namedeli = deliveryRepo.getAllNamedelivery();
		aa:
		for(String name : namedeli) {
			delis = deliveryRepo.getBynameDelivery(name);
			bb:
			for(delivery deli : delis) {
				if(userorder.getTotalWeight()<=deli.getMaxWeight()) {
					delilist.add(deli);
					System.out.println(deli.getNameDelivery()+deli.getMaxWeight());
					break bb; 
				}
			}
		}
		model.addAttribute("delilist", delilist);
		model.addAttribute("id", idorder);
		return "cartdelivery";
	}
	
	@PostMapping("/savecartdelivery/{idorder}")
	public String savecartdelivery(@PathVariable("idorder") String idorder
			,@RequestParam(name = "delivery") Integer delivery) {
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(Integer.parseInt(idorder)).get();
		delivery deli = new delivery();
		deli = deliveryRepo.getByidDelivery(delivery);
		userorder.setNameDelivery(deli.getNameDelivery());
		userorder.setTotalOrder(userorder.getTotalOrder()+deli.getPriceDelivery());
		userorderRepo.save(userorder);
		return "redirect:/cartconfirm/"+idorder;
	}
	
	@GetMapping("/confirmorder/{idorder}")
	public String confirmorder(@PathVariable("idorder") String idorder
			,@ModelAttribute("orderid") OrderId Sorderid) {
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(Integer.parseInt(idorder)).get();
		userorder.setStatus("paying");
		userorder.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(userorder);
		//set stock
		List<orderdetail> orders = new ArrayList<orderdetail>();
		orders = orderdetailRepo.getByIdorder(Integer.parseInt(idorder));
		productdetail product = new productdetail();
		for(orderdetail order : orders) {
			product = productdetailRepo.findById(order.getProductdetail().getIdProduct()).get();
			if(order.getSize()!=null) {
				if(order.getSize().equalsIgnoreCase("s")) {
					product.setS(product.getS()-order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("m")) {
					product.setM(product.getM()-order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("l")) {
					product.setL(product.getL()-order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("xl")) {
					product.setXl(product.getXl()-order.getNumber());
				}
			}else {
				product.setNumberStock(product.getNumberStock()-order.getNumber());
			}
			productdetailRepo.save(product);
		}
		Sorderid.setOrderid("noid");
		return "redirect:/buyproduct";
	}
	
	@GetMapping("productajax")
	@ResponseBody
	public List<Productjson> productajax(@RequestParam int id){
		Productjson pjson = new Productjson();
		List<Productjson> jsList = new ArrayList<Productjson>();
		productdetail product = new productdetail();
		product = productdetailRepo.getByIdproduct(id);
		pjson.setSstock(product.getS());
		pjson.setMstock(product.getM());
		pjson.setLstock(product.getL());
		pjson.setXLstock(product.getXl());
		jsList.add(pjson);
		return jsList;
	}
	
}
