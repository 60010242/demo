package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.productdetail;

public interface ProductDetailRepository extends JpaRepository<productdetail, Integer> {
	
	@Query("from productdetail p where p.category = :namecat order by p.nameProduct")
	List<productdetail> getByCategory(@Param("namecat")String namecat);
	
	@Query("from productdetail p where p.idProduct = :idProduct")
	productdetail getByIdproduct(@Param("idProduct")int idProduct);

	@Modifying
	@Transactional
	@Query (nativeQuery = true, value="DELETE FROM productdetail WHERE id_product = :idProduct")
	void deleteByIdProduct(@Param("idProduct")String idProduct);
}
