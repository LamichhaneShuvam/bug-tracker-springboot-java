package com.lymn.bugTracker.service;

import java.util.List;

import com.lymn.bugTracker.model.Project;

public interface ProjectService {
	List<Project> getAllProject();
	Project getProjectById(Long id);
	void save(Project project);
	boolean isPresent(Long projectId, Long userId, String role);
}
