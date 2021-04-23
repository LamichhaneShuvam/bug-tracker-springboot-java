package com.lymn.bugTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.MessageRepository;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.ProjectRepository;

@Service
public class DashboardService {
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	BugRepository bugRepository;
	@Autowired 
	ModifiedRepository modifiedRepository;
	@Autowired
	MessageRepository messageRepository;
	
	public Long projectCount() {
		return projectRepository.count();
	}
	public Long bugCount() {
		return bugRepository.count();
	}
	public Long messageCount() {
		return messageRepository.count();
	}
}
