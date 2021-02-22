package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.delivery;

public interface DeliveryRepository extends JpaRepository<delivery, String> {

	@Query("select distinct nameDelivery from delivery order by nameDelivery")
	List<String> getAllNamedelivery();
	
	@Query("from delivery order by nameDelivery,maxWeight")
	List<delivery> findAllOrderByNameDeli();
	
	@Query("from delivery d where d.nameDelivery = :nameDelivery order by d.maxWeight")
	List<delivery> getBynameDelivery(@Param("nameDelivery")String nameDelivery);
	
	@Query("from delivery d where d.idDelivery = :idDelivery")
	delivery getByidDelivery(@Param("idDelivery")int idDelivery);
	
	@Modifying
	@Transactional
	@Query (nativeQuery = true, value="DELETE FROM delivery WHERE id_delivery = :idDelivery")
	void deleteByIdDelivery(@Param("idDelivery")int idDelivery);
}
