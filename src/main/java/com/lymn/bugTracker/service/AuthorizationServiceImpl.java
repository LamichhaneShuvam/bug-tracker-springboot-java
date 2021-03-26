package com.lymn.bugTracker.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.lymn.bugTracker.repository.ModifiedRepository;

public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	ModifiedRepository modifiedRepository;
	
	@Override
	public boolean bugIsPresent(Long id, String role, Long userId ) {
		if(role.equals("ADMIN")||role.equals("MANAGER")) {
			return true;
		}
		else {
			return modifiedRepository.bugIsPresent(id, userId);
		}
	}

	@Override
	public boolean projectIsPresent(Long id, String role) {
		if(role.equals("ADMIN")||role.equals("MANAGER")) {
			return true;
		}
		return false;
	}
}
