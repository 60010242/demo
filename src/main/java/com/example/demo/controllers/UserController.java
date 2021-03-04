package com.example.demo.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.CreateFile;
import com.example.demo.Sellinfo;
import com.example.demo.Transgroup;
import com.example.demo.entities.account;
import com.example.demo.entities.notification;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.useraddress;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.UserAddressRepository;
import com.example.demo.repositories.UserOrderRepository;
import com.example.demo.repositories.UserProfileRepository;

@Controller
public class UserController {					// about user and user tables
	@Autowired
	private UserProfileRepository userprofileRepo;
	
	@Autowired
	private UserAddressRepository useraddressRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private UserOrderRepository userorderRepo;
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	
	@Autowired
	private NotificationRepository notiRepo;
	
	@GetMapping("/signup")
	public String greeting(Model model) {
		model.addAttribute("user", new userprofile());
		return "signup";
	}
	
	@PostMapping("/savemem")
	public String saveMember(@ModelAttribute userprofile user
			,@RequestParam(name = "newaddress") String newaddress
			, BindingResult errors
			, Model model
			,@RequestParam("imageFile") MultipartFile file) throws IOException  {
		userprofile profile = new userprofile();
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			user.setPhotoUser(image);
		}
		user.setType("Customer");				//Customer
		user.setCoin(0);
		profile = userprofileRepo.save(user);
		useraddress address = new useraddress();
		address.setAddress(newaddress);
		address.setIdUser(profile.getIdUser());
		useraddressRepo.save(address);
		model.addAttribute("message", "You have already signed.");
		model.addAttribute("user", new userprofile());
		return "signup";
	}
	
	@GetMapping("/myinfo")
	public String myinfo(@SessionAttribute("user") userprofile user, Model model) {
		int countnoti = 0;
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		if(user.getType().equalsIgnoreCase("Seller")) {
			countnoti = notiRepo.countAllBysubjectSeller();
			notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		}else {
			countnoti = notiRepo.countAllBysubjectCustomer(user.getIdUser());
			notilist = notiRepo.getBysubjectCustomer(user.getIdUser(), PageWithnineElements);
		}
		model.addAttribute("sourceweb", "/myinfo");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		return "myinfo";
	}
	
	@GetMapping("/sellinfo")
	public String sellinfo(@SessionAttribute("user") userprofile user, Model model) {
		List<userprofile> sellerlist = new ArrayList<userprofile>();
		userprofile seller = new userprofile();
		sellerlist = userprofileRepo.findOneByType("Seller");
		seller = sellerlist.get(0);
		List<useraddress> a = new ArrayList<useraddress>();
		List<Sellinfo> addresses = new ArrayList<Sellinfo>();
		int i = 1;
		a = useraddressRepo.getByIdUser(seller.getIdUser());
		for(useraddress a1 : a) {
			Sellinfo s = new Sellinfo();
			s.setIndex(i);
			s.setAddress(a1.getAddress());
			addresses.add(s);
			i++;
		}
		int countnoti = notiRepo.countAllBysubjectCustomer(user.getIdUser());
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectCustomer(user.getIdUser(), PageWithnineElements);
		model.addAttribute("sourceweb", "/sellinfo");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		model.addAttribute("seller", seller);
		model.addAttribute("addresses", addresses);
		return "sellinfo";
	}
	
	@GetMapping("/sellaccount")
	public String sellaccount(Model model) {
		List<account> accounts = new ArrayList<account>();
		accounts = accountRepo.findAllOrderByNameBank();
		model.addAttribute("accounts", accounts);
		return "sellaccount";
	}
	
	@GetMapping("/edit")
	public String editProfile(@SessionAttribute("user") userprofile user, Model model) {
		System.out.println("Edit id:"+ user.getIdUser());
		int countnoti = 0;
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		if(user.getType().equalsIgnoreCase("Seller")) {
			countnoti = notiRepo.countAllBysubjectSeller();
			notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		}else {
			countnoti = notiRepo.countAllBysubjectCustomer(user.getIdUser());
			notilist = notiRepo.getBysubjectCustomer(user.getIdUser(), PageWithnineElements);
		}
		model.addAttribute("sourceweb", "/edit");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
	
	@PostMapping("/saveaddress2/{idorder}")
	public ModelAndView saveaddress(@RequestParam(name = "newaddress") String newaddress
			,@PathVariable("idorder") String idorder
			,@SessionAttribute("user") userprofile user
			,RedirectAttributes model) {
		useraddress address = new useraddress();
		address.setAddress(newaddress);
		address.setIdUser(user.getIdUser());
		useraddressRepo.save(address);

		String str = "/cartaddress/"+idorder;
		model.addFlashAttribute("urlredirect", str);
		model.addFlashAttribute("overlayshow", "visible");
		return new ModelAndView("redirect:/cartconfirm/"+idorder);
	}
	
	@GetMapping("/deleteaddress/{idorder}/{idaddress}")
	public ModelAndView deleteaddress(@PathVariable("idaddress") Integer idaddress
			,@PathVariable("idorder") String idorder, RedirectAttributes model) {
		useraddressRepo.deleteByIdAddress(idaddress);
		
		String str = "/cartaddress/"+idorder;
		model.addFlashAttribute("urlredirect", str);
		model.addFlashAttribute("overlayshow", "visible");
		return new ModelAndView("redirect:/cartconfirm/"+idorder);
	}
	
	@PostMapping("/search")
	public String search(@RequestParam(name = "type") String type
			,@RequestParam(name = "code") String code
			,Model model) {
		String text ="";
		boolean search = false;
		int sendcost = 0;
		userorder userorder = new userorder();
		List<orderdetail> orders = new ArrayList<orderdetail>();
		List<userprofile> users = new ArrayList<userprofile>();
		if(type.equalsIgnoreCase("ordernum")) {
			if(userorderRepo.getByIdOrder(Integer.parseInt(code))!=null) {
				userorder = userorderRepo.getByIdOrder(Integer.parseInt(code));
				orders = orderdetailRepo.getByIdorder(Integer.parseInt(code));
				int totalOrder = 0;
				for(orderdetail order : orders) {
					totalOrder = totalOrder + (order.getRealPrice()*order.getNumber());
				}
				
				sendcost = userorder.getTotalOrder()-totalOrder;
				search = true;
			}
			text = "เลขออเดอร์";
		}else if(type.equalsIgnoreCase("namebuyer")) {
			if(userprofileRepo.findByname(code)!=null) {
				users = userprofileRepo.findByname(code);
				search = true;
			}
			text = "ชื่อผู้ซื้อ";
		}
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/firstpage");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		model.addAttribute("userorder", userorder);
		model.addAttribute("orders", orders);
		model.addAttribute("users", users);
		model.addAttribute("text", "ไม่มีข้อมูลของ "+ text +" "+code);
		model.addAttribute("type", type);
		model.addAttribute("search", search);
		model.addAttribute("sendcost", sendcost);
		return "/showsearch";
	}
	
	@GetMapping("/showordersearch/{iduser}")
	public String showorderuser(@PathVariable("iduser") Integer iduser
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<Transgroup> transgroup = new ArrayList<Transgroup>();
		userorders = userorderRepo.getByidUser(iduser);
		for(userorder userorder : userorders) {
			Transgroup tran = new Transgroup();
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			String img = orderdetails.get(0).getProductdetail().getPhotoProduct();
			tran.setUserorder(userorder);
			tran.setImage(img);
			//tran.getUserorder().getIdUser()
			//tran.getImage()
			transgroup.add(tran);
		}
		model.addAttribute("transgroup", transgroup);
		return "/showordersearch";
	}
	
	@GetMapping("/trantotracking4/{idorder}")
	public String trantotracking3(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("tracking");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		notification noti = new notification();
		notification setnoti = new notification();
		if(notiRepo.getByIdorder(idorder)!=null) {
			noti = notiRepo.getByIdorder(idorder);
			setnoti = notiRepo.findById(noti.getIdNoti()).get();
		}else {
			setnoti.setIdOrder(idorder);
			userorder notiorder = new userorder();
			notiorder = userorderRepo.getByIdOrder(idorder);
			setnoti.setIdUser(notiorder.getUserprofile().getIdUser());
		}
		setnoti.setSubject("การชำระเงินสำเร็จแล้ว");
		setnoti.setMessage("ผู้ขายยืนยันการชำระเงินของคำสั่งซื้อ "+idorder+" แล้ว");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
		return "redirect:/firstpage";
	}
	
	@PostMapping("/trantoshipping2/{idorder}")
	public String trantocomplete(@PathVariable("idorder") Integer idorder
			,@RequestParam("numtrack") String numtrack) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setTrack(numtrack);
		order.setStatus("shipping");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		notification noti = new notification();
		notification setnoti = new notification();
		if(notiRepo.getByIdorder(idorder)!=null) {
			noti = notiRepo.getByIdorder(idorder);
			setnoti = notiRepo.findById(noti.getIdNoti()).get();
		}else {
			setnoti.setIdOrder(idorder);
			userorder notiorder = new userorder();
			notiorder = userorderRepo.getByIdOrder(idorder);
			setnoti.setIdUser(notiorder.getUserprofile().getIdUser());
		}
		setnoti.setSubject("สินค้าถูกจัดส่งแล้ว");
		setnoti.setMessage("พัสดุหมายเลข "+numtrack+" ของคำสั่งซื้อ "+idorder+" จัดส่งแล้ว");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
		return "redirect:/firstpage";
	}
}