package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.userorder;

public interface UserOrderRepository extends JpaRepository<userorder, Integer> {
	
	@Query("from userorder o where o.status = :status order by o.payTime asc")
	List<userorder> getByStatus(@Param("status")String status);
	
	@Query("from userorder o where o.status = :status and o.idUser = :idUser order by o.payTime asc")
	List<userorder> getByStatusAndIduser(@Param("status")String status,@Param("idUser")int idUser);
	
	@Query("from userorder o where o.status = :status1 or o.status = :status2 order by o.payTime asc, o.status")
	List<userorder> getByTwoStatus(@Param("status1")String status1,@Param("status2")String status2);

	@Query("from userorder o where o.status = :status order by o.payTime asc, o.sellerBank asc")
	List<userorder> getByStatusorderbySellerBank(@Param("status")String status);
	
	@Query("from userorder o where o.nameDelivery = :transport and o.status = :status order by o.payTime asc")
	List<userorder> getByTrackingNamedelivery(@Param("transport")String transport,@Param("status")String status);

	@Query("select MIN(o.payTime) from userorder o where o.status = :status")
	LocalDateTime findMinPaytime(@Param("status")String status);
	
	@Query("select MIN(o.payTime) from userorder o where o.status = :status or o.status = :status2")
	LocalDateTime findMinPaytimeTwoStatus(@Param("status")String status,@Param("status2")String status2);

	@Query("from userorder o where o.idOrder = :idOrder")
	userorder getByIdOrder(@Param("idOrder")int idOrder);
	
	@Query("from userorder o where o.idUser = :idUser and o.status is not null")
	List<userorder> getByidUser(@Param("idUser")int idUser);
	
	@Query("from userorder o where o.status = :status and o.sellerBank = :bank order by o.payTime asc")
	List<userorder> getByStatusandBank(@Param("status")String status,@Param("bank")String bank);

	@Query("select COUNT(*) from userorder o where o.status = :status")
	int countAllBystatus(@Param("status")String status);
	
	@Query("select COUNT(*) from userorder o where o.status = :status and o.idUser = :idUser")
	int countAllBystatusAndIduser(@Param("status")String status,@Param("idUser")int idUser);
	
	@Query("select COUNT(*) from userorder o where o.status = :status and o.sellerBank = :bank")
	int countAllBystatusAndSellerbank(@Param("status")String status,@Param("bank")String bank);
	
	@Query("select COUNT(*) from userorder o where o.status = :status and o.nameDelivery = :transport")
	int countAllBystatusAndNamedelivery(@Param("status")String status,@Param("transport")String transport);

	@Modifying
	@Transactional
	@Query (nativeQuery = true, value="DELETE FROM userorder WHERE id_order = :idOrder")
	void deleteByIdorder(@Param("idOrder")int idOrder);
}
