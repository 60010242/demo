package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.notification;

public interface NotificationRepository extends JpaRepository<notification, Integer>{
	
	@Query("from notification n where n.idOrder = :idOrder")
	notification getByIdorder(@Param("idOrder")int idOrder);
}
