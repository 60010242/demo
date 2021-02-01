package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.orderdetail;
import com.example.demo.entities.productdetail;
import com.example.demo.entities.userorder;
import com.example.demo.entities.userprofile;
import com.example.demo.repositories.OrderDetailRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.repositories.UserOrderRepository;
import com.example.demo.repositories.UserProfileRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

@Service
public class CreatePDF {
	@Autowired
	private UserProfileRepository userprofileRepo;
	
	@Autowired
	private ProductDetailRepository productRepo;
	
	@Autowired
	private UserOrderRepository userorderRepo;
	
	@Autowired
	private OrderDetailRepository orderdetailRepo;
	
	public void printOrder(Document document,String transport)throws DocumentException {
		FontFactory.register("D:\\front\\THSarabunNew.ttf", "sarabun");
		Font boldFont = FontFactory.getFont("sarabun", "TIS-620",true, Font.BOLD);
		Font textFont = FontFactory.getFont("sarabun", "TIS-620",true, Font.NORMAL);
		Font headFont = FontFactory.getFont("sarabun", "TIS-620",true, Font.BOLD);
		List<userprofile> sellerlist = new ArrayList<userprofile>();
		userprofile seller = new userprofile();
		sellerlist = userprofileRepo.findOneByType("Seller");
		seller = sellerlist.get(0);
		List<userorder> userorders = new ArrayList<userorder>();
		List<orderdetail> orderlists = new ArrayList<orderdetail>();
		System.out.println(transport);
		userorders = userorderRepo.getByTrackingNamedelivery(transport,"tracking");
		for(userorder userorder : userorders) {
			List<orderdetail> orderdetails = new ArrayList<orderdetail>();
			orderdetails = orderdetailRepo.getByIdorder(userorder.getIdOrder());
			orderlists.addAll(orderdetails);
		}
		
		for (int index = 0; index < userorders.size(); index++) {
			System.out.println("create table");
			userorder userorder1 = new userorder();
			userorder1 = userorders.get(index);
			List<orderdetail> orderlist1 = new ArrayList<orderdetail>();
			for(orderdetail order : orderlists) {
				if(order.getIdOrder()==userorder1.getIdOrder()) {
					orderlist1.add(order);
				}
			}
				
			Paragraph hpara = new Paragraph();
			headFont.setSize(30);
			hpara.add(new Paragraph(userorder1.getNameDelivery(),headFont));
			float [] userColumnWidths = {1f,4f};
			PdfPTable table1 = new PdfPTable(userColumnWidths);
			table1.setWidthPercentage(90f);
			textFont.setSize(20);
			boldFont.setSize(20);
			hpara.add(new Paragraph("ORDER #"+Integer.toString(userorder1.getIdOrder()),boldFont));
			addEmptyLine(hpara, 1);
			
			insertCell(table1,"ผู้ส่ง (from)",Element.ALIGN_LEFT,1,textFont);
			insertCell(table1,seller.getAddress(),Element.ALIGN_LEFT,1,textFont);
			
			insertCell(table1,"ผู้รับ (to)",Element.ALIGN_LEFT,1,textFont);
			insertCell(table1,userorder1.getUserprofile().getAddress(),Element.ALIGN_LEFT,1,textFont);
				
			hpara.add(table1);
			addEmptyLine(hpara, 1);
			float [] orderColumnWidths = {1f,3f,1f};
			PdfPTable table2 = new PdfPTable(orderColumnWidths);
			table2.setWidthPercentage(90f);
			
			insertCell(table2,"#",Element.ALIGN_CENTER,1,boldFont);
			insertCell(table2,"รายการ",Element.ALIGN_CENTER,1,boldFont);
			insertCell(table2,"จำนวน",Element.ALIGN_CENTER,1,boldFont);
			
			int Norder = 1;
			int num = 0;
			for(orderdetail order : orderlist1) {
				productdetail product = new productdetail();
				product = productRepo.getOne(order.getIdProduct());
				insertCell(table2,Integer.toString(Norder),Element.ALIGN_CENTER,1,textFont);
				insertCell(table2,product.getNameProduct(),Element.ALIGN_LEFT,1,textFont);
				insertCell(table2,Integer.toString(order.getNumber()),Element.ALIGN_CENTER,1,textFont);
				Norder++;
				num+=order.getNumber();
			}
			insertCell(table2,"รวม",Element.ALIGN_CENTER,2,textFont);
			insertCell(table2,Integer.toString(num),Element.ALIGN_CENTER,1,textFont);
			hpara.add(table2);
			
			if(index%2 == 0 && index+1 < userorders.size()){
				System.out.println("samepage"+index);
				addEmptyLine(hpara, 1);
				Paragraph text = new Paragraph("-------------------------------------------------------------------------------------------------------------------",boldFont);
				text.setAlignment(Element.ALIGN_CENTER);
				hpara.add(text);
			}
			document.add(hpara);
			
			if(index%2 != 0 && index+1 < userorders.size()) {		//first order in page
				System.out.println("newpage"+index);
				document.newPage();
			}
		}
		
	}
	
	private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){
		   
		  //create a new cell with the specified Text and Font
		  PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		  //set the cell alignment
		  cell.setHorizontalAlignment(align);
		  //set the cell column span in case you want to merge two or more cells
		  cell.setColspan(colspan);
		  //in case there is no text and you wan to create an empty row
		  if(text.trim().equalsIgnoreCase("")){
		   cell.setMinimumHeight(10f);
		  }
		  //add the call to the table
		  table.addCell(cell);
		   
		 }
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
	}
}
