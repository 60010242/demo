package com.example.demo.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Checkjason;
import com.example.demo.CreateFile;
import com.example.demo.Daygroup;
import com.example.demo.Monthgroup;
import com.example.demo.Orderjson;
import com.example.demo.Tab;
import com.example.demo.Transgroup;
import com.example.demo.Userjson;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.useraddress;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.DeliveryRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.UserAddressRepository;
import com.example.demo.repositories.UserOrderRepository;
import com.example.demo.repositories.UserProfileRepository;
import com.example.demo.services.CreatePDF;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class OrderController {
	@Autowired
	private UserAddressRepository useraddressRepo;
	
	@Autowired
	private UserProfileRepository userprofileRepo;
	
	@Autowired
	private UserOrderRepository userorderRepo;
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	
	@Autowired
	private ProductDetailRepository productRepo;
	
	@Autowired
	private CreatePDF pdf;
	
	@Autowired
	private DeliveryRepository deliRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@GetMapping("/checking")
	public String findchecking(Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatusorderbySellerBank("checking");
		LocalDateTime minDate = userorderRepo.findMinPaytime("checking");
		LocalDate localDate = LocalDate.now();
		List<Daygroup> daylist = new ArrayList<Daygroup>();
		localDate = localDate.plusDays(1);
		if(minDate != null) {
			for(LocalDate date = minDate.toLocalDate(); date.isBefore(localDate); date = date.plusDays(1)) {
				boolean dayhave = false;
				Daygroup daygroup = new Daygroup();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				String mixname = format.format(date);
				daygroup.setDatenum(date.getDayOfMonth());
				daygroup.setMonthnum(date.getMonthValue());
				daygroup.setLocaldate(date);
				daygroup.setMixname(mixname);
				for(userorder userorder : userorders) {
					if(userorder.getPayTime().toLocalDate().isEqual(date)) {
						dayhave = true;
					}
				}
				if(dayhave) {
					daylist.add(0, daygroup);
				}
			}
		}
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<String> accounts = new ArrayList<String>();
		accounts = accountRepo.getAllNamesellaccount();
		List<Tab> tabs = new ArrayList<Tab>();
		for(String account : accounts) {
			Tab tab = new Tab();
			tab.setNumorder(userorderRepo.countAllBystatusAndSellerbank("checking", account));
			tab.setNametab(account);
			tabs.add(tab);
		}
		int Allorder = userorderRepo.countAllBystatus("checking");
		int index = 1;
		model.addAttribute("tabs", tabs);
		model.addAttribute("Allorder", Allorder);
		model.addAttribute("index", index);
		model.addAttribute("accounts", accounts);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercheck";
	}
	
	@GetMapping("/checking/{namebank}")
	public String findcheckingbyname(@PathVariable("namebank") String namebank
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatusandBank("checking", namebank);
		LocalDateTime minDate = userorderRepo.findMinPaytime("checking");
		LocalDate localDate = LocalDate.now();
		List<Daygroup> daylist = new ArrayList<Daygroup>();
		localDate = localDate.plusDays(1);
		if(minDate != null) {
			for(LocalDate date = minDate.toLocalDate(); date.isBefore(localDate); date = date.plusDays(1)) {
				boolean dayhave = false;
				Daygroup daygroup = new Daygroup();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				String mixname = format.format(date);
				daygroup.setDatenum(date.getDayOfMonth());
				daygroup.setMonthnum(date.getMonthValue());
				daygroup.setLocaldate(date);
				daygroup.setMixname(mixname);
				for(userorder userorder : userorders) {
					if(userorder.getPayTime().toLocalDate().isEqual(date)) {
						dayhave = true;
					}
				}
				if(dayhave) {
					daylist.add(0, daygroup);
				}
			}
		}
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<String> accounts = new ArrayList<String>();
		accounts = accountRepo.getAllNamesellaccount();
		List<Tab> tabs = new ArrayList<Tab>();
		for(String account : accounts) {
			Tab tab = new Tab();
			tab.setNumorder(userorderRepo.countAllBystatusAndSellerbank("checking", account));
			tab.setNametab(account);
			tabs.add(tab);
		}
		int Allorder = userorderRepo.countAllBystatus("checking");
		int index = 2;
		model.addAttribute("tabs", tabs);
		model.addAttribute("Allorder", Allorder);
		model.addAttribute("index", index);
		model.addAttribute("accounts", accounts);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercheck";
	}
	
	@GetMapping("/trantotracking/{idorder}")
	public String trantotracking(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("tracking");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/checking";
	}
	
	@GetMapping("/trantotracking2/{idorder}")
	public String trantotracking2(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("tracking");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/cancel";
	}
	
	@PostMapping("/trantoshipping/{idorder}")
	public String trantocomplete(@PathVariable("idorder") Integer idorder
			,@RequestParam("numtrack") String numtrack) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setTrack(numtrack);
		order.setStatus("shipping");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/tracking";
	}
	
	@GetMapping("/tracking")
	public String findtracking(Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatus("tracking");
		LocalDateTime minDate = userorderRepo.findMinPaytime("tracking");
		LocalDate localDate = LocalDate.now();
		List<Daygroup> daylist = new ArrayList<Daygroup>();
		localDate = localDate.plusDays(1);
		if(minDate != null) {
			for(LocalDate date = minDate.toLocalDate(); date.isBefore(localDate); date = date.plusDays(1)) {
				boolean dayhave = false;
				Daygroup daygroup = new Daygroup();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				String mixname = format.format(date);
				daygroup.setDatenum(date.getDayOfMonth());
				daygroup.setMonthnum(date.getMonthValue());
				daygroup.setLocaldate(date);
				daygroup.setMixname(mixname);
				for(userorder userorder : userorders) {
					if(userorder.getPayTime().toLocalDate().isEqual(date)) {
						dayhave = true;
					}
				}
				if(dayhave) {
					daylist.add(0, daygroup);
				}
			}
		}
		
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<String> deliverys = new ArrayList<String>();
		deliverys = deliRepo.getAllNamedelivery();
		List<Tab> tabs = new ArrayList<Tab>();
		for(String delivery : deliverys) {
			Tab tab = new Tab();
			tab.setNumorder(userorderRepo.countAllBystatusAndNamedelivery("tracking", delivery));
			tab.setNametab(delivery);
			tabs.add(tab);
		}
		int Allorder = userorderRepo.countAllBystatus("tracking");
		int index = 1;
		model.addAttribute("tabs", tabs);
		model.addAttribute("Allorder", Allorder);
		model.addAttribute("index", index);
		model.addAttribute("deliverys", deliverys);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfertrack";
	}
	
	@GetMapping("/tracking/{namedeli}")
	public String findtrackingbydeli(@PathVariable("namedeli") String namedeli
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByTrackingNamedelivery(namedeli, "tracking");
		LocalDateTime minDate = userorderRepo.findMinPaytime("tracking");
		LocalDate localDate = LocalDate.now();
		List<Daygroup> daylist = new ArrayList<Daygroup>();
		localDate = localDate.plusDays(1);
		if(minDate != null) {
			for(LocalDate date = minDate.toLocalDate(); date.isBefore(localDate); date = date.plusDays(1)) {
				boolean dayhave = false;
				Daygroup daygroup = new Daygroup();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				String mixname = format.format(date);
				daygroup.setDatenum(date.getDayOfMonth());
				daygroup.setMonthnum(date.getMonthValue());
				daygroup.setLocaldate(date);
				daygroup.setMixname(mixname);
				for(userorder userorder : userorders) {
					if(userorder.getPayTime().toLocalDate().isEqual(date)) {
						dayhave = true;
					}
				}
				if(dayhave) {
					daylist.add(0, daygroup);
				}
			}
		}
		
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<String> deliverys = new ArrayList<String>();
		deliverys = deliRepo.getAllNamedelivery();
		List<Tab> tabs = new ArrayList<Tab>();
		for(String delivery : deliverys) {
			Tab tab = new Tab();
			tab.setNumorder(userorderRepo.countAllBystatusAndNamedelivery("tracking", delivery));
			tab.setNametab(delivery);
			tabs.add(tab);
		}
		int Allorder = userorderRepo.countAllBystatus("tracking");
		int index = 2;
		model.addAttribute("tabs", tabs);
		model.addAttribute("Allorder", Allorder);
		model.addAttribute("index", index);
		model.addAttribute("deliverys", deliverys);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfertrack";
	}
	
	@GetMapping("/complete/{status}")
	public String findcomplete(@PathVariable("status") String status
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatus(status);
		//"shipping", "complete"
		LocalDate localDate = LocalDate.now();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		List<Monthgroup> monthlist = new ArrayList<Monthgroup>();
		List<userorder> ul = new ArrayList<userorder>();
		for(int i=1;i<8;i++) {
			boolean m = false;
			Monthgroup monthgroup = new Monthgroup();
			String monthname = getMonth(month)+ ' ' + year;
			monthgroup.setMonthname(monthname);
			monthgroup.setMonthnum(month);
			
			for(userorder uo : userorders) {
				if(uo.getPayTime().getMonthValue()==month) {
					if(uo.getPayTime().getYear()==year) {
						ul.add(uo);
						m = true;
					}
				}
			}
			if(m) {
				monthlist.add(monthgroup);
			}
			month--;
			if(month==0) {
				month = 12;
				year--;
			}
		}
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<Integer> numorders = new ArrayList<Integer>();
		int numship = userorderRepo.countAllBystatus("shipping");
		numorders.add(numship);
		int numcom = userorderRepo.countAllBystatus("complete");
		numorders.add(numcom);
		model.addAttribute("numorders", numorders);
		model.addAttribute("userlist", ul);
		model.addAttribute("monthlist", monthlist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercomplete";
	}
	
	@GetMapping("/cancel/{status}")
	public String findcansel(@PathVariable("status") String status
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatus(status);
		//"cancel", "contact","transferred"
		LocalDateTime minDate = userorderRepo.findMinPaytimeTwoStatus("cancel", "contact");
		LocalDate localDate = LocalDate.now();
		List<Daygroup> daylist = new ArrayList<Daygroup>();
		localDate = localDate.plusDays(1);
		if(minDate != null) {
			for(LocalDate date = minDate.toLocalDate(); date.isBefore(localDate); date = date.plusDays(1)) {
				boolean dayhave = false;
				Daygroup daygroup = new Daygroup();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				String mixname = format.format(date);
				daygroup.setDatenum(date.getDayOfMonth());
				daygroup.setMonthnum(date.getMonthValue());
				daygroup.setLocaldate(date);
				daygroup.setMixname(mixname);
				for(userorder userorder : userorders) {
					if(userorder.getPayTime().toLocalDate().isEqual(date)) {
						dayhave = true;
					}
				}
				if(dayhave) {
					daylist.add(0, daygroup);
				}
			}
		}
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<Integer> numorders = new ArrayList<Integer>();
		int numcancel = userorderRepo.countAllBystatus("cancel");
		numorders.add(numcancel);
		int numcon = userorderRepo.countAllBystatus("contact");
		numorders.add(numcon);
		int numtran = userorderRepo.countAllBystatus("transferred");
		numorders.add(numtran);
		model.addAttribute("numorders", numorders);
		model.addAttribute("status",status);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercancel";
	}
	
	@GetMapping("/trantocancel/{idorder}")
	public String trantocancel(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("cancel");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/tracking";
	}
	
	@PostMapping("/trantocontact/{idorder}")
	public String trantocontact(@PathVariable("idorder") Integer idorder
			,@RequestParam("detailcancel") String detailcancel) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("contact");
		order.setDetailcancel(detailcancel);
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/cancel/cancel";
	}
	
	@GetMapping("/trantotransferred/{idorder}")
	public String trantotransferred(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("transferred");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/cancel/contact";
	}
	
	@GetMapping("/buytransfer")
	public String buytransfer(@SessionAttribute("user") userprofile user,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<Transgroup> transgroup = new ArrayList<Transgroup>();
		userorders = userorderRepo.getByStatusAndIduser("paying", user.getIdUser());
		for(userorder u : userorders) {
			LocalDateTime to = LocalDateTime.now();
			long days = ChronoUnit.DAYS.between(u.getCratedOrder(), to);
			System.out.println(days);
			if(days>2) {
				userorder reuser = new userorder();
				reuser = userorderRepo.findById(u.getIdOrder()).get();
				reuser.setStatus("notpay");
				reuser.setPayTime(LocalDateTime.now());
				reuser.setCratedOrder(LocalDateTime.now());
				userorderRepo.save(reuser);
			}
		}
		userorders = userorderRepo.getByStatusAndIduser("paying", user.getIdUser());
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
		return "buytransfer";
	}
	
	@PostMapping("/savetransfer")
	public String savetransfer(@RequestParam(name = "sendid") int sendid
			,@RequestParam(name = "userbank") String userbank
			,@RequestParam(name = "sellbank") String sellbank
			,@RequestParam(name = "pay") int pay
			,@RequestParam(name = "paydatetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paydatetime
			,@RequestParam("imageFile") MultipartFile file) throws IOException {
		userorder userorder = new userorder();
		userorder = userorderRepo.findById(sendid).get();
		userorder.setCratedOrder(LocalDateTime.now());
		userorder.setUserBank(userbank);
		userorder.setSellerBank(sellbank);
		userorder.setPayTotal(pay);
		userorder.setPayTime(paydatetime);
		userorder.setStatus("checking");
		if(!file.isEmpty()) {
			CreateFile bFile = new CreateFile();
			String image = bFile.invertfile(file);
			userorder.setPhotoPay(image);
		}
		userorderRepo.save(userorder);
		return "redirect:/buytransfer";
	}

	@GetMapping("/trantonotpay/{idorder}")
	public String trantonotpay(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("notpay");
		order.setPayTime(LocalDateTime.now());
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/buytransfer";
	}
	
	@GetMapping("/trantopaying/{idorder}")
	public String trantopaying(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("paying");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/buytransfer";
	}
	
	@GetMapping("/buytrack/{state}")
	public String buytrack(@SessionAttribute("user") userprofile userprofile
			,@PathVariable("state") Integer state
			,Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<userorder> Alluserorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatusAndIduser("shipping", userprofile.getIdUser());
		for(userorder u : userorders) {
			LocalDateTime to = LocalDateTime.now();
			long days = ChronoUnit.DAYS.between(u.getCratedOrder(), to);
			System.out.println(days);
			if(days>10) {
				userorder user = new userorder();
				user = userorderRepo.findById(u.getIdOrder()).get();
				user.setStatus("complete");
				user.setCratedOrder(LocalDateTime.now());
				userorderRepo.save(user);
			}
		}
		if(state == 1) {
			userorders = userorderRepo.getByStatusAndIduser("checking", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
			userorders = userorderRepo.getByStatusAndIduser("tracking", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
		}else if(state == 2) {
			userorders = userorderRepo.getByStatusAndIduser("shipping", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
			userorders = userorderRepo.getByStatusAndIduser("complete", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
		}else if(state == 3) {
			userorders = userorderRepo.getByStatusAndIduser("cancel", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
			userorders = userorderRepo.getByStatusAndIduser("contact", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
			userorders = userorderRepo.getByStatusAndIduser("transferred", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
			userorders = userorderRepo.getByStatusAndIduser("notpay", userprofile.getIdUser());
			Alluserorders.addAll(userorders);
		}
		
		LocalDate localDate = LocalDate.now();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		List<Monthgroup> monthlist = new ArrayList<Monthgroup>();
		List<userorder> ul = new ArrayList<userorder>();
		for(int i=1;i<8;i++) {
			boolean m = false;
			Monthgroup monthgroup = new Monthgroup();
			String monthname = getMonth(month)+ ' ' + year;
			monthgroup.setMonthname(monthname);
			monthgroup.setMonthnum(month);
			
			for(userorder uo : Alluserorders) {
				if(uo.getPayTime().getMonthValue()==month) {
					if(uo.getPayTime().getYear()==year) {
						ul.add(uo);
						m = true;
					}
				}
			}
			if(m) {
				monthlist.add(monthgroup);
			}
			month--;
			if(month==0) {
				month = 12;
				year--;
			}
		}
		for(userorder userorder : Alluserorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		List<Integer> numorders = new ArrayList<Integer>();
		int num1 = userorderRepo.countAllBystatus("checking")+userorderRepo.countAllBystatus("tracking");
		numorders.add(num1);
		int num2 = userorderRepo.countAllBystatus("shipping")+userorderRepo.countAllBystatus("complete");
		numorders.add(num2);
		int num3 = userorderRepo.countAllBystatus("transferred")+userorderRepo.countAllBystatus("cancel")+userorderRepo.countAllBystatus("contact")+userorderRepo.countAllBystatus("notpay");
		numorders.add(num3);
		model.addAttribute("numorders", numorders);
		model.addAttribute("state", state);
		model.addAttribute("userlist", ul);
		model.addAttribute("monthlist", monthlist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", Alluserorders);
		return "buytrack";
	}

	@GetMapping("/trantocomplete/{idorder}")
	public String trantocomplete(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("complete");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/buytrack";
	}
	
	@GetMapping("/createPDF")
	public String createPDF(Model model) throws IOException {
		List<String> deliverylist = new ArrayList<String>();
		deliverylist = deliRepo.getAllNamedelivery();
		String transport = deliverylist.get(0);
		String filepath = "Desktop" + transport + ".pdf";
		String encoded = "";
		int check = 0;
		List<userorder> userorders = new ArrayList<userorder>();
		userorders = userorderRepo.getByTrackingNamedelivery(transport,"tracking");
		if(userorders.size()>0) {
			try {
				Document document = new Document(PageSize.A4);
				PdfWriter.getInstance(document, new FileOutputStream(filepath));
				document.open();
				pdf.printOrder(document, transport);
				document.close();
				byte[] inFileBytes = Files.readAllBytes(Paths.get(filepath)); 
				encoded = Base64.getEncoder().encodeToString(inFileBytes);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			check = 1;
		}else {
			check = 0;
			encoded = "OUT OF ORDER";
		}
		List<userprofile> sellerlist = new ArrayList<userprofile>();
		userprofile seller = new userprofile();
		sellerlist = userprofileRepo.findOneByType("Seller");
		seller = sellerlist.get(0);
		List<useraddress> selladdress = new ArrayList<useraddress>();
		selladdress = useraddressRepo.getByIdUser(seller.getIdUser());
		boolean address = false;
		if(selladdress.get(0)!=null) {
			address = true;
		}
		model.addAttribute("address", address);
		model.addAttribute("deliverylist", deliverylist);
		model.addAttribute("pdf", encoded);
		model.addAttribute("check", check);
		return "printall";
	}
	
	@GetMapping("/createPDF/{transport}")
	public String createPDF2(@PathVariable("transport") String transport
			,Model model) throws IOException {
		String filepath = "Desktop" + transport + ".pdf";
		List<String> deliverylist = new ArrayList<String>();
		deliverylist = deliRepo.getAllNamedelivery();
		String encoded = "";
		int check = 0;
		List<userorder> userorders = new ArrayList<userorder>();
		userorders = userorderRepo.getByTrackingNamedelivery(transport,"tracking");
		if(userorders.size()>0) {
			try {
				Document document = new Document(PageSize.A4);
				PdfWriter.getInstance(document, new FileOutputStream(filepath));
				document.open();
				pdf.printOrder(document, transport);
				document.close();
				byte[] inFileBytes = Files.readAllBytes(Paths.get(filepath)); 
				encoded = Base64.getEncoder().encodeToString(inFileBytes);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			check = 1;
		}else {
			check = 0;
			encoded = "OUT OF ORDER";
		}
		List<userprofile> sellerlist = new ArrayList<userprofile>();
		userprofile seller = new userprofile();
		sellerlist = userprofileRepo.findOneByType("Seller");
		seller = sellerlist.get(0);
		List<useraddress> selladdress = new ArrayList<useraddress>();
		selladdress = useraddressRepo.getByIdUser(seller.getIdUser());
		boolean address = false;
		if(selladdress.get(0)!=null) {
			address = true;
		}
		model.addAttribute("address", address);
		model.addAttribute("deliverylist", deliverylist);
		model.addAttribute("pdf", encoded);
		model.addAttribute("check", check);
		return "printall";
	}
	
	public String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month-1];
	}
	
	@GetMapping("/gotoajax")
	public String gotoajax() {
			return "testajax";
	}
	
	@GetMapping("/testajax")
	@ResponseBody
	public List<Orderjson> getFoos(@RequestParam int id) {
		Orderjson json = new Orderjson();
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(id);
	    List<Orderjson> jsList = new ArrayList<Orderjson>();
	    for(orderdetail order : orderlist) {
	    	json = new Orderjson();
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	if(order.getSize()!=null) {
	    		json.setName(product.getNameProduct()+" "+order.getSize().toUpperCase());
	    	}else {
	    		json.setName(product.getNameProduct());
	    	}
	    	json.setNum(order.getNumber());
	    	json.setCost(order.getRealPrice());
	    	json.setImage(order.getProductdetail().getPhotoProduct());
	    	jsList.add(json);
	    }
		return jsList;
	}
	
	@GetMapping("/userajax")
	@ResponseBody
	public List<Userjson> userajax(@RequestParam int id) {
		Userjson user = new Userjson();
		userorder userorder = new userorder();
		userorder = userorderRepo.getByIdOrder(id);
		user.setId(userorder.getIdOrder());
		user.setUsername(userorder.getUserprofile().getName());
		user.setTotal(userorder.getTotalOrder());
		user.setAddress(userorder.getUserAddress());
		user.setTrack(userorder.getTrack());
		if(userorder.getDetailcancel()!=null) {
			user.setDetailcancel(userorder.getDetailcancel());
		}
		
		List<orderdetail> orders = new ArrayList<orderdetail>();
		orders = orderdetailRepo.getByIdorder(id);
		int totalOrder = 0;
		for(orderdetail order : orders) {
			totalOrder = totalOrder + (order.getRealPrice()*order.getNumber());
		}
		
		user.setSendcost(userorder.getTotalOrder()-totalOrder);
		List<Userjson> jsList = new ArrayList<Userjson>();
		jsList.add(user);
		return jsList;
	}
	
	@GetMapping("/checkajax")
	@ResponseBody
	public List<Checkjason> checkajax(@RequestParam int id) {
		Checkjason user = new Checkjason();
		userorder userorder = new userorder();
		userorder = userorderRepo.getByIdOrder(id);
		user.setId(userorder.getIdOrder());
		user.setUsername(userorder.getUserprofile().getName());
		user.setTotal(userorder.getTotalOrder());
		user.setPay(userorder.getPayTotal());
		user.setUserbank(userorder.getUserBank());
		user.setSellerbank(userorder.getSellerBank());
		user.setPaytime(userorder.getPayTime());
		user.setImage(userorder.getPhotoPay());
		List<Checkjason> jsList = new ArrayList<Checkjason>();
		jsList.add(user);
		return jsList;
	}
	
	@GetMapping("/accountajax")
	@ResponseBody
	public List<String> accountajax(){
		List<String> nameaccount = new ArrayList<String>();
		nameaccount = accountRepo.getAllNamesellaccount();
		return nameaccount;
	}
}
