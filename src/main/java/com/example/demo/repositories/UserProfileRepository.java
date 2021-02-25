package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.userprofile;

public interface UserProfileRepository extends JpaRepository<userprofile, Integer> {

	List<userprofile> findByemail(String email);
	
	@Query("from userprofile o where o.type = :type")
	List<userprofile> findOneByType(@Param("type")String type);
	
	@Query("from userprofile o where o.name = :name")
	List<userprofile> findByname(@Param("name")String name);
}
