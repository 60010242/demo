package com.example.demo.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Daygroup;
import com.example.demo.Mixjson;
import com.example.demo.Monthgroup;
import com.example.demo.Orderjson;
import com.example.demo.Userjson;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userorder;
import com.example.demo.repositories.DeliveryRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.UserOrderRepository;
import com.example.demo.services.CreatePDF;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class OrderController {
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
	
	@GetMapping("/packing")
	public String findpacking(Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByStatus("packing");
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "packing";
	}
	
	@PostMapping("/trantocomplete/{idorder}")
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
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfertrack";
	}
	
	@GetMapping("/complete")
	public String findcomplete(Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByTwoStatus("shipping", "complete");
		LocalDate localDate = LocalDate.now();
		int month = localDate.getMonthValue();
		int year = localDate.getYear();
		List<Monthgroup> monthlist = new ArrayList<Monthgroup>();
		List<userorder> ul = new ArrayList<userorder>();
		for(int i=1;i<8;i++) {
			Monthgroup monthgroup = new Monthgroup();
			String monthname = getMonth(month)+ ' ' + year;
			monthgroup.setMonthname(monthname);
			monthgroup.setMonthnum(month);
			monthlist.add(monthgroup);
			for(userorder uo : userorders) {
				if(uo.getPayTime().getMonthValue()==month) {
					if(uo.getPayTime().getYear()==year) {
						ul.add(uo);
					}
				}
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
		model.addAttribute("userlist", ul);
		model.addAttribute("monthlist", monthlist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercomplete";
	}
	
	@GetMapping("/cancel")
	public String findcansel(Model model) {
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		userorders = userorderRepo.getByTwoStatus("cancel", "contact");
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
	
	@GetMapping("/trantocontact/{idorder}")
	public String trantocontact(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("contact");
		order.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(order);
		return "redirect:/cancel";
	}
	
	@GetMapping("/createPDF")
	public String createPDF(Model model) throws IOException {
		List<String> deliverylist = new ArrayList<String>();
		deliverylist = deliRepo.getAllNamedelivery();
		String transport = deliverylist.get(0);
		String filepath = "D:/filefromspring/" + transport + ".pdf";
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
		
		model.addAttribute("deliverylist", deliverylist);
		model.addAttribute("pdf", encoded);
		model.addAttribute("check", check);
		return "printall";
	}
	
	@GetMapping("/createPDF/{transport}")
	public String createPDF2(@PathVariable("transport") String transport
			,Model model) throws IOException {
		String filepath = "D:/filefromspring/" + transport + ".pdf";
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
	    	json.setName(product.getNameProduct());
	    	json.setNum(order.getNumber());
	    	json.setCost(order.getRealPrice());
	    	jsList.add(json);
	    }
		return jsList;
	}
	
	@GetMapping("/userajax")
	@ResponseBody
	public Userjson userajax(@RequestParam int id) {
		Userjson user = new Userjson();
		userorder userorder = new userorder();
		userorder = userorderRepo.getByIdOrder(id);
		user.setId(userorder.getIdOrder());
		user.setUsername(userorder.getUserprofile().getName());
		user.setTotal(userorder.getTotalOrder());
		user.setAddress(userorder.getUserprofile().getAddress());
		user.setTrack(userorder.getTrack());
		return user;
	}
	
	@GetMapping("/gettwoajax")
	@ResponseBody
	public Mixjson gettwoajax(@RequestParam int id) {
		Mixjson mix = new Mixjson();
		Userjson user = new Userjson();
		userorder userorder = new userorder();
		userorder = userorderRepo.getByIdOrder(id);
		user.setId(userorder.getIdOrder());
		user.setUsername(userorder.getUserprofile().getName());
		user.setTotal(userorder.getTotalOrder());
		user.setAddress(userorder.getUserprofile().getAddress());
		user.setTrack(userorder.getTrack());
		Orderjson json = new Orderjson();
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(id);
	    List<Orderjson> jsList = new ArrayList<Orderjson>();
	    for(orderdetail order : orderlist) {
	    	json = new Orderjson();
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	json.setName(product.getNameProduct());
	    	json.setNum(order.getNumber());
	    	json.setCost(order.getRealPrice());
	    	jsList.add(json);
	    }
	    mix.setUser(user);
	    mix.setOrderlist(jsList);
		return mix;
	}
}
