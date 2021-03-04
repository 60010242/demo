package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.notification;
import com.example.demo.entities.orderdetail;
import com.example.demo.entities.userorder;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.UserOrderRepository;

@Controller
public class NotificationController {
	@Autowired
	private NotificationRepository notiRepo;
	
	@Autowired
	private UserOrderRepository userorderRepo;
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	
	@GetMapping("/notidirect/{idnoti}")
	public String notidirect(@PathVariable("idnoti") Integer idnoti, Model model) {
		notification noti = new notification();
		noti = notiRepo.findById(idnoti).get();
		noti.setUserread(1);
		notiRepo.save(noti);
		int sendcost = 0;
		userorder userorder = new userorder();
		List<orderdetail> orders = new ArrayList<orderdetail>();
		if(userorderRepo.getByIdOrder(noti.getIdOrder())!=null) {
			userorder = userorderRepo.getByIdOrder(noti.getIdOrder());
			orders = orderdetailRepo.getByIdorder(noti.getIdOrder());
			int totalOrder = 0;
			for(orderdetail order : orders) {
				totalOrder = totalOrder + (order.getRealPrice()*order.getNumber());
			}
			sendcost = userorder.getTotalOrder()-totalOrder;
		}
		model.addAttribute("idnoti", idnoti);
		model.addAttribute("userorder", userorder);
		model.addAttribute("orders", orders);
		model.addAttribute("sendcost", sendcost);
		return "notidirect";
	}
	
	@GetMapping("/readnoti/{sourceweb}/{idnoti}")
	public String readnoti(@PathVariable("sourceweb") String sourceweb
			,@PathVariable("idnoti") Integer idnoti) {
		System.out.println(sourceweb);
		notification noti = new notification();
		noti = notiRepo.findById(idnoti).get();
		noti.setUserread(1);
		notiRepo.save(noti);
		return "redirect:/"+sourceweb;
	}
	
	@GetMapping("/readnoti/{sourceweb}/{state}/{idnoti}")
	public String readnoti(@PathVariable("sourceweb") String sourceweb
			,@PathVariable("state") String state
			,@PathVariable("idnoti") Integer idnoti) {
		System.out.println(sourceweb+" "+state);
		notification noti = new notification();
		noti = notiRepo.findById(idnoti).get();
		noti.setUserread(1);
		notiRepo.save(noti);
		return "redirect:/"+sourceweb+"/"+state;
	}
	
	@GetMapping("/readnoti/{sourceweb}/{state}/{state2}/{idnoti}")
	public String readnoti(@PathVariable("sourceweb") String sourceweb
			,@PathVariable("state") String state
			,@PathVariable("state2") String state2
			,@PathVariable("idnoti") Integer idnoti) {
		System.out.println(sourceweb+" "+state+" "+state2);
		notification noti = new notification();
		noti = notiRepo.findById(idnoti).get();
		noti.setUserread(1);
		notiRepo.save(noti);
		return "redirect:/"+sourceweb+"/"+state+"/"+state2;
	}
	
	@GetMapping("/trantotracking3/{idnoti}/{idorder}")
	public String trantotracking3(@PathVariable("idorder") Integer idorder
			,@PathVariable("idnoti") Integer idnoti) {
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
		return "redirect:/notidirect/"+idnoti;
	}
	
	@PostMapping("/trantoshipping/{idnoti}/{idorder}")
	public String trantocomplete(@PathVariable("idorder") Integer idorder
			,@PathVariable("idnoti") Integer idnoti
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
		return "redirect:/notidirect/"+idnoti;
	}
}
