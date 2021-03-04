package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.notification;

public interface NotificationRepository extends JpaRepository<notification, Integer>{
	
	@Query("from notification n where n.idOrder = :idOrder")
	notification getByIdorder(@Param("idOrder")int idOrder);
	
	@Query("select COUNT(*) from notification n where n.subject = 'ตรวจสอบการชำระเงิน' or n.subject = 'จัดส่งสำเร็จ' and n.userread = 0")
	int countAllBysubjectSeller();
	
	@Query("select COUNT(*) from notification n where n.userread = 0 and n.idUser = :idUser and (n.subject = 'การชำระเงินสำเร็จแล้ว' or n.subject = 'สินค้าถูกจัดส่งแล้ว' or n.subject = 'คำสั่งซื้อถูกยกเลิก' or n.subject = 'ผู้ขายติดต่อแล้ว' or n.subject = 'ผู้ขายโอนเงินคืนแล้ว')")
	int countAllBysubjectCustomer(@Param("idUser")int idUser);


	@Query("from notification n where n.subject = 'ตรวจสอบการชำระเงิน' or n.subject = 'จัดส่งสำเร็จ' order by n.createdNoti desc")
	List<notification> getBysubjectSeller(Pageable pageable);
	
	@Query("from notification n where n.idUser = :idUser and (n.subject = 'การชำระเงินสำเร็จแล้ว' or n.subject = 'สินค้าถูกจัดส่งแล้ว' or n.subject = 'คำสั่งซื้อถูกยกเลิก' or n.subject = 'ผู้ขายติดต่อแล้ว' or n.subject = 'ผู้ขายโอนเงินคืนแล้ว') order by n.createdNoti desc")
	List<notification> getBysubjectCustomer(@Param("idUser")int idUser,Pageable pageable);
	
	@Query("from notification n where n.subject = 'ตรวจสอบการชำระเงิน' or n.subject = 'จัดส่งสำเร็จ' order by n.createdNoti desc")
	List<notification> getAllBysubjectSeller();
	
	@Query("from notification n where n.idUser = :idUser and (n.subject = 'การชำระเงินสำเร็จแล้ว' or n.subject = 'สินค้าถูกจัดส่งแล้ว' or n.subject = 'คำสั่งซื้อถูกยกเลิก' or n.subject = 'ผู้ขายติดต่อแล้ว' or n.subject = 'ผู้ขายโอนเงินคืนแล้ว') order by n.createdNoti desc")
	List<notification> getAllBysubjectCustomer(@Param("idUser")int idUser);
}
