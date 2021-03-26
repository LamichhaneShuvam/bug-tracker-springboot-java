package com.lymn.bugTracker.service;

public interface AuthorizationService {
	
	boolean bugIsPresent(Long id, String role, Long userId);
	boolean projectIsPresent(Long id, String role);
	

}
