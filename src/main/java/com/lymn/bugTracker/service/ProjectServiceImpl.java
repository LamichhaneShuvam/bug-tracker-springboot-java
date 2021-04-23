package com.lymn.bugTracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lymn.bugTracker.model.Project;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.ProjectRepository;

@Service
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ModifiedRepository modifiedRepository;
	
	@Override
	public List<Project> getAllProject() {
		List<Project> projects = projectRepository.findByStatus("ongoing");//instead of all do by status
		return projects;
	}
	@Override
	public Project getProjectById(Long id) {
		return projectRepository.findById(id).get();
	}
	@Override
	public void save(Project project) {
		projectRepository.save(project);
	}
	@Override
	public boolean isPresent(Long projectId, Long userId, String role) {
		if(role.equals("ADMIN") || role.equals("MANAGER")) {
			return true;
		}else {	
			return modifiedRepository.projectIsPresent(projectId, userId);
		}
	}

}
