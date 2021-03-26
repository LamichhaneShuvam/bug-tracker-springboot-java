package com.lymn.bugTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lymn.bugTracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u From User u WHERE u.email =?1")
	User findByEmail(String email);
	
	@Query("SELECT id FROM User u WHERE u.email = ?1")
	Long findIdByEmail(String email);

	@Query("SELECT firstName FROM User u WHERE u.email = ?1")
	String findNameByEmail(String email);
	
	//Set<Project> findProjectByUserId(Long id);
}
