package com.lymn.bugTracker.service;

import java.util.List;

import com.lymn.bugTracker.model.Bug;

public interface BugService {
	List<Bug> getBugByProjectId(Long id);
	void save(Bug bug);
	boolean isPresent(Long bugId, Long userId);
	List<Bug> getBugByUserId(Long projectId, Long userId);
	Bug getBugById(Long id);

}
