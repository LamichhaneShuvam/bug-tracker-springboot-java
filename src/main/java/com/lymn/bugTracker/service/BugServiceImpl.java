package com.lymn.bugTracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lymn.bugTracker.model.Bug;
import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.ModifiedRepository;

@Service
public class BugServiceImpl implements BugService {
	@Autowired
	BugRepository BugRepository;
	
	
	@Autowired
	ModifiedRepository modifiedRepository;

	@Override
	public List<Bug> getBugByProjectId(Long id){
		//return BugRepository.findByProjectId(id);
		return modifiedRepository.findBugByStatus(id, "ongoing");
	}
	
	@Override
	public void save(Bug bug) {
		modifiedRepository.saveBug(bug);
	}
	
	@Override
	public boolean isPresent(Long bugId, Long userId) {
		return modifiedRepository.bugIsPresent(bugId, userId);
	}
	@Override
	public List<Bug> getBugByUserId(Long projectId, Long userId){
		return modifiedRepository.findBugByUserId(projectId, userId);
	}
	public Bug getBugById(Long id){
		return BugRepository.findById(id).get();
	}
}
