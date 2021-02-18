package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.useraddress;

public interface UserAddressRepository extends JpaRepository<useraddress,Integer>{

	@Query("from useraddress a where a.idUser = :idUser")
	List<useraddress> getByIdUser(@Param("idUser")int idUser);
	
	@Query("select COUNT(*) from useraddress a where a.idUser = :idUser")
	int countAddress(@Param("idUser")int idUser); // and a.enable = 1
	
	@Modifying
	@Transactional
	@Query (nativeQuery = true, value="DELETE FROM useraddress WHERE id_address = :idAddress")
	void deleteByIdAddress(@Param("idAddress")int idAddress);
}
