package com.lymn.bugTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lymn.bugTracker.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	

}
