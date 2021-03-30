package com.lymn.bugTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lymn.bugTracker.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long>{
//	@Query("SELECT new File(d.id, d.name, d.size) FROM File d ORDER BY d.uploadTime DESC")
//	List<File> findAll(); 
	
	List<Message> findByBugId(Long id);
	//add find by userId also in the future..
}
