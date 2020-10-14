package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.productdetail;

public interface ProductDetailRepository extends JpaRepository<productdetail, Integer> {
	
	@Query("from productdetail p where p.category = :namecat order by p.nameProduct")
	List<productdetail> getByCategory(@Param("namecat")String namecat);
}
