package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.orderdetail;
import com.example.demo.entities.orderdetailid;

public interface OrderDetailRepository extends JpaRepository<orderdetail, orderdetailid> {
	
	@Query("from orderdetail d where d.idOrder = :idOrder order by d.noOrder")
	List<orderdetail> getByIdorder(@Param("idOrder")int idOrder);
	
	@Query("select MAX(d.noOrder) from orderdetail d where d.idOrder = :idOrder")
	int findMaxNoOrder(@Param("idOrder")int idOrder);
	
	@Modifying
	@Transactional
	@Query (nativeQuery = true, value="DELETE FROM orderdetail WHERE id_product = :idProduct")
	void deleteByIdProduct(@Param("idProduct")String idProduct);
}
