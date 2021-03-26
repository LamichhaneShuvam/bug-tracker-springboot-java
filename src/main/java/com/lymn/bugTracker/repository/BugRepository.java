package com.lymn.bugTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lymn.bugTracker.model.Bug;

public interface BugRepository extends JpaRepository<Bug, Long>{

	List<Bug> findByProjectId(Long id);

}
