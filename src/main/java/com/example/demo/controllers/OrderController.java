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
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Checkjason;
import com.example.demo.CreateFile;
import com.example.demo.Daygroup;
import com.example.demo.Monthgroup;
import com.example.demo.Orderjson;
import com.example.demo.Tab;
import com.example.demo.Transgroup;
import com.example.demo.Userjson;
import com.example.demo.entities.notification;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.smtp;
import com.example.demo.entities.useraddress;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.DeliveryRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.SmtpRepository;
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
	private SmtpRepository smtpRepo;
	
	@Autowired
	private CreatePDF pdf;
	
	@Autowired
	private DeliveryRepository deliRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private NotificationRepository notiRepo;
	
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/checking");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/checking/"+namebank);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
	public String trantotracking(@PathVariable("idorder") Integer idorder
			,@SessionAttribute("user") userprofile user) {

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
		return "redirect:/cancel/cancel";
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/tracking");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/tracking/"+namedeli);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		userorders = userorderRepo.getByStatus("shipping");
		for(userorder u : userorders) {
			LocalDateTime to = LocalDateTime.now();
			long days = ChronoUnit.DAYS.between(u.getCratedOrder(), to);
			System.out.println(days);
			if(days>14) {
				userorder user = new userorder();
				user = userorderRepo.findById(u.getIdOrder()).get();
				user.setStatus("complete");
				user.setCratedOrder(LocalDateTime.now());
				userorderRepo.save(user);
			}
		}
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/complete/"+status);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		int countnoti = notiRepo.countAllBysubjectSeller();
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectSeller(PageWithnineElements);
		model.addAttribute("sourceweb", "/cancel/"+status);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		model.addAttribute("numorders", numorders);
		model.addAttribute("status",status);
		model.addAttribute("daylist", daylist);
		model.addAttribute("orderlists", orderlists);
		model.addAttribute("userorders", userorders);
		return "transfercancel";
	}
	
	@GetMapping("/trantocancel/{idorder}")
	public ModelAndView trantocancel(@PathVariable("idorder") Integer idorder
			,@SessionAttribute("user") userprofile user
				, HttpServletRequest request
				, RedirectAttributes modelfl) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("cancel");
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
		setnoti.setSubject("คำสั่งซื้อถูกยกเลิก");
		setnoti.setMessage("คำสั่งซื้อ "+idorder+" ถูกยกเลิก");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
		
		if(smtpRepo.count() != 0) {
			if(smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get().isUsable() == true) {
				String customermail = userprofileRepo.findById(userorderRepo.findById(idorder).get().getIdUser()).get().getEmail(),
					sellermail = smtpRepo.findAll().get(0).getGmail();
				toSendMail(2, idorder, sellermail, request.getLocalName());
				toSendMail(4, idorder, customermail, request.getLocalName());
				modelfl.addFlashAttribute("message", "ส่งข้อความการยกเลิกออเดอร์เข้าสู่ Gmail ของท่านแล้ว");
			}
		}
		
		return new ModelAndView("redirect:/tracking");
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
		setnoti.setSubject("ผู้ขายติดต่อแล้ว");
		setnoti.setMessage("ผู้ขายติดต่อเผื่อโอนเงินของคำสั่งซื้อ "+idorder+" คืน");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
		return "redirect:/cancel/cancel";
	}
	
	@GetMapping("/trantotransferred/{idorder}")
	public String trantotransferred(@PathVariable("idorder") Integer idorder) {
		userorder order = new userorder(); 
		order = userorderRepo.findById(idorder).get();
		order.setStatus("transferred");
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
		setnoti.setSubject("ผู้ขายโอนเงินคืนแล้ว");
		setnoti.setMessage("ผู้ขายโอนเงินของคำสั่งซื้อ "+idorder+" คืนแล้ว");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
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
		int countnoti = notiRepo.countAllBysubjectCustomer(user.getIdUser());
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectCustomer(user.getIdUser(), PageWithnineElements);
		model.addAttribute("sourceweb", "/buytransfer");
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
		model.addAttribute("transgroup", transgroup);
		return "buytransfer";
	}
	
	@PostMapping("/savetransfer")
	public ModelAndView savetransfer(@RequestParam(name = "sendid") int sendid
			,@RequestParam(name = "userbank") String userbank
			,@RequestParam(name = "sellbank") String sellbank
			,@RequestParam(name = "pay") int pay
			,@RequestParam(name = "paydatetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paydatetime
			,@RequestParam("imageFile") MultipartFile file
			,@SessionAttribute("user") userprofile user
			, RedirectAttributes modelfl
			, HttpServletRequest request) throws IOException {
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
		
		notification noti = new notification();
		noti.setSubject("ตรวจสอบการชำระเงิน");
		noti.setMessage("กรุณาตรวจสอบการชำระเงินของคำสั่งซื้อ "+sendid);
		noti.setUserread(0);
		noti.setCreatedNoti(LocalDateTime.now());
		noti.setIdOrder(sendid);
		noti.setIdUser(user.getIdUser());
		notiRepo.save(noti);
		
		System.out.println(smtpRepo.count());
		if(smtpRepo.count() != 0) {
			if(smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get().isUsable() == true) {
				String customermail = userprofileRepo.findById(userorderRepo.findById(sendid).get().getIdUser()).get().getEmail(),
					sellermail = smtpRepo.findAll().get(0).getGmail();
				toSendMail(1, sendid, sellermail, request.getLocalName());
				toSendMail(3, sendid, customermail, request.getLocalName());
				modelfl.addFlashAttribute("message", "ส่งรายการคำสั่งซื้อเข้าสู่ E-mail ของท่านแล้ว");
			}
			
		}
		return new ModelAndView("redirect:/buytransfer");
	}

	@GetMapping("/trantonotpay/{idorder}")
	public String trantonotpay(@PathVariable("idorder") Integer idorder) {
		userorder uorder = new userorder(); 
		uorder = userorderRepo.findById(idorder).get();
		uorder.setStatus("notpay");
		uorder.setPayTime(LocalDateTime.now());
		uorder.setCratedOrder(LocalDateTime.now());
		userorderRepo.save(uorder);
		List<orderdetail> orders = new ArrayList<orderdetail>();
		orders = orderdetailRepo.getByIdorder(idorder); 
		for(orderdetail order : orders) {
			productdetail product = new productdetail();
			product = productRepo.findById(order.getProductdetail().getIdProduct()).get();
			if(order.getSize()!=null) {
				if(order.getSize().equalsIgnoreCase("s")) {
					product.setS(product.getS()+order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("m")) {
					product.setM(product.getM()+order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("l")) {
					product.setL(product.getL()+order.getNumber());
				}else if(order.getSize().equalsIgnoreCase("xl")) {
					product.setXl(product.getXl()+order.getNumber());
				}
			}else {
				product.setNumberStock((product.getNumberStock()+order.getNumber()));
			}
			productRepo.save(product);
		}
		return "redirect:/buytransfer";
	}
	
	@GetMapping("/buytrack/{state}")
	public String buytrack(@SessionAttribute("user") userprofile userprofile
			,@PathVariable("state") Integer state
			,Model model) {
		List<userorder> Alluserorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		List<userorder> userorders = new ArrayList<userorder>();
		userorders = userorderRepo.getByStatusAndIduser("shipping", userprofile.getIdUser());
		for(userorder u : userorders) {
			LocalDateTime to = LocalDateTime.now();
			long days = ChronoUnit.DAYS.between(u.getCratedOrder(), to);
			System.out.println(days);
			if(days>14) {
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
		int countnoti = notiRepo.countAllBysubjectCustomer(userprofile.getIdUser());
		List<notification> notilist = new ArrayList<notification>();
		Pageable PageWithnineElements = PageRequest.of(0, 15);
		notilist = notiRepo.getBysubjectCustomer(userprofile.getIdUser(), PageWithnineElements);
		model.addAttribute("sourceweb", "/buytrack/"+state);
		model.addAttribute("notilist", notilist);
		model.addAttribute("countnoti", countnoti);
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
		setnoti.setSubject("จัดส่งสำเร็จ");
		setnoti.setMessage("คำสั่งซื้อ "+idorder+" จัดส่งสำเร็จแล้ว");
		setnoti.setUserread(0);
		setnoti.setCreatedNoti(LocalDateTime.now());
		notiRepo.save(setnoti);
		return "redirect:/buytrack/2";
	}
	
	@GetMapping("/createPDF")
	public String createPDF(Model model
			,@SessionAttribute("user") userprofile user) throws IOException {
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
				pdf.printOrder(document, transport,user);
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
		
		List<useraddress> selladdress = new ArrayList<useraddress>();
		selladdress = useraddressRepo.getByIdUser(user.getIdUser());
		boolean address = false;
		if(selladdress!=null) {
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
			,Model model
			,@SessionAttribute("user") userprofile user) throws IOException {
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
				pdf.printOrder(document, transport,user);
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
		List<useraddress> selladdress = new ArrayList<useraddress>();
		selladdress = useraddressRepo.getByIdUser(user.getIdUser());
		boolean address = false;
		if(selladdress!=null) {
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
	
	
	
	/* ======================================== SMTP ======================================== */
	
	
	public String toSendMail(Integer msg_type, Integer idorder, String target_mail, String origin) {
		/* msg_type
		 * 0 = ผู้ขายลงทะเบียน Auto send mail
		 * 1 = ผู้ขาย มีรายการคำสั่งซื้อใหม่
		 * 2 = ผู้ขาย ทำการยกเลิกสินค้า
		 * 3 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 4 = ผู้ซื้อ ถูกยกเลิกสินค้า*/
		
		
		String base_url = "http://" +origin+ ":7070";
		String msg_subject, msg_body;
		
		msg_subject = mailText(msg_type ,idorder, base_url)[0];
		msg_body = mailText(msg_type ,idorder, base_url)[1];
		
		System.out.println(msg_subject +"\n\n"+ msg_body);
		
		return sendEmail(target_mail, msg_subject, msg_body);
	}
	
	
	String sendEmail(String target_mail, String subject, String body_text) {
		
		// seller session ..will use from database
		smtp smtp_have = new smtp();
		smtp_have = smtpRepo.findById(smtpRepo.findAll().get(0).getIdSmtp()).get();
		
		String resulttxt = "",
				username = smtp_have.getGmail(),
				password = smtp_have.getPasswordgen(); //fmrftkyefqyonglu
		
		
		Properties props = new Properties();
		
		// set SMTP seller to send G-mail
		props.put("mail.host", "smtp.gmail.com");
		props.put("mail.port", "587");
		props.put("mail.username", username);
		props.put("mail.password", password);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.timeout", "5000");
		props.put("mail.smtp.writetimeout", "5000");
		props.put("mail.smtp.connectiontimeout", "5000");
		props.put("mail.smtp.starttls.enable", "true");
		
		Session mailSession = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		
		mailSession.setDebug(true);
		
		// Send mail
		MimeMessage msg = new MimeMessage(mailSession);

        try {
        	// target mail
			msg.setRecipients(Message.RecipientType.TO, target_mail);
			msg.setSubject(subject); //subject mail
			msg.setContent(body_text, "text/html; charset=utf-8"); //text will send to customer

			Transport.send(msg);
			
			resulttxt = "Success";
			System.out.println(resulttxt);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulttxt = "Fail";
			System.out.println(resulttxt);
		}
        
        return resulttxt;
	}
	
	
	// Message Send
	String[] mailText(Integer msg_type, Integer idorder, String base_url) {
		
		
		/* msg_type
		 * 0 = ผู้ขายลงทะเบียน Auto send mail
		 * 1 = ผู้ขาย มีรายการคำสั่งซื้อใหม่, ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 2 = ผู้ขาย ทำการยกเลิกสินค้า, ผู้ซื้อ ถูกยกเลิกสินค้า
		 * 3 = ผู้ซื้อ แจ้งเตือนรายการคำสั่งซื้อ
		 * 4 = ผู้ซื้อ ถูกยกเลิกสินค้า*/
		
		String[] subject_list = {
				//seller
				"การลงทะเบียนส่ง Gmail อัตโนมัติเสร็จสิ้น",
				"ท่านได้รับคำสั่งซื้อใหม่ ออเดอร์ #", "ท่านได้ทำการยกเลิกออเดอร์ #",
				//customer
				"รายการคำสั่งซื้อของท่าน ออเดอร์ #", "แจ้งเตือนถูกยกเลิกออเดอร์ #"};
		
		String[] header_list = {
				"การลงทะเบียนส่งข้อความอัตโนมัติของท่านเสร็จสิ้น",
				//seller
				"ออเดอร์ #", "ทำการยกเลิกออเดอร์ #",
				//customer
				"ออเดอร์ #", "ผู้ขายได้ทำการยกเลิกออเดอร์ #"};
		
		String[] footer_list = {
				//seller
				"",
				"\n\nราคารวมทั้งสิ้น ",
				"\n\nต้องคืนเงินรวมทั้งสิ้น ",
				//customer
				"\n\nราคารวมทั้งสิ้น ",
				"\n\nท่านจะได้รับเงินคืนจำนวน "};
		
		String[] url_list = {
				"",
				base_url +"/checking",
				base_url +"/cancel/cancel",
				base_url +"/buytransfer",
				base_url
		};
		String css =
				"<style>"
				+ "table{ width: 500px; border: 1px solid gray; }"
				+ "table tr:first-child td{ background-color: gray; color: white; }"
				+ "table td:first-child{ width: 55%; }"
				+ "table td:NOT(first-child){ width: 15%; }"
				+ "table tr:last-child td{ background-color: lightgray; color: black; font-weight: bold; }"
				+ "</style>";
				
		List<orderdetail> orderlist = new ArrayList<orderdetail>();
		orderlist = orderdetailRepo.getByIdorder(idorder);
		
		System.out.println(idorder+"list is "+orderlist);
		
	    String myorder = "\n<br/><table>"
	    		+ "<tr><td align='center' colspan='4'>รายการสินค้า</td></tr>";
	    Integer totalprice = 0;
	    
	    // order detail text in mail
	    for(orderdetail order : orderlist) {
	    	productdetail product = productRepo.getByIdproduct(order.getIdProduct());
	    	if(order.getSize() != null) {
	    		myorder += "\n<tr><td>"+product.getNameProduct()
	    				+"</td>\n<td>ไซส์ "+order.getSize().toUpperCase()
	    	    		+"</td>\n<td>x"+order.getNumber()
	    	    		+"</td>\n<td>"+order.getRealPrice()+" บาท</td>";
	    		totalprice += order.getRealPrice();
	    	}else {
	    		myorder += "\n<tr><td colspan='2'>"+product.getNameProduct()
	    				+"</td>\n<td>x"+order.getNumber()
	    				+"</td>\n<td>"+order.getRealPrice()+" บาท</td>";
	    		totalprice += order.getRealPrice();
	    	}
	    	myorder += "\n";
	    }
	    
	    myorder += "\n<tr><td colspan='3'>รวม</td>\n<td>"+ totalprice +".-</td></tr></table>\n<br/>";
	    myorder += footer_list[msg_type] + totalprice +" บาท\n\n<br/><br/>"+ url_list[msg_type];
	   	
	    String[] datasend;
	    
		if(idorder != 0 && base_url != "") {
			datasend = new String[]{
				subject_list[msg_type] + idorder,
				css+"<body>"+header_list[msg_type] + idorder + myorder+"</body>"};

		}
		else {
			datasend = new String[]{
					subject_list[msg_type],
					css+"<body>"+header_list[msg_type]+"</body>"};
		}
		return datasend;
	}
}
