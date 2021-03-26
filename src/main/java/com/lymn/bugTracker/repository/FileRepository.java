package com.lymn.bugTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lymn.bugTracker.model.File;

public interface FileRepository extends JpaRepository<File, Long>{
//	@Query("SELECT new File(d.id, d.name, d.size) FROM File d ORDER BY d.uploadTime DESC")
//	List<File> findAll(); 
	
	List<File> findByBugId(Long id);
}
