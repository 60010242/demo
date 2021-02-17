package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.useraddress;

public interface UserAddressRepository extends JpaRepository<useraddress,Integer>{

	@Query("from useraddress a where a.idUser = :idUser")
	List<useraddress> getByIdUser(@Param("idUser")int idUser);
	
	@Query("select COUNT(*) from useraddress a where a.idUser = :idUser and a.enable = 1")
	int countAddress(@Param("idUser")int idUser);
}
