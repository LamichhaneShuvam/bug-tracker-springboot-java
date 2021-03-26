package com.lymn.bugTracker.dto;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;

import com.lymn.bugTracker.repository.UserRepository;

@Configuration
public class UserDto {
	@Autowired
	UserRepository userRepository;
	
	@Bean                 //is it a bean or not
	@Scope("prototype")
	public HttpSession primaryInit(Authentication authentication, HttpSession httpSession) {
		httpSession.setAttribute("uId", userRepository.findIdByEmail(authentication.getName()));
		httpSession.setAttribute("userName", userRepository.findNameByEmail(authentication.getName()));
		httpSession.setAttribute("loggedInUserEmail", authentication.getName());
		httpSession.setAttribute("role", authentication.getAuthorities().stream().findFirst().get().toString());
		return httpSession;
	}	
}
