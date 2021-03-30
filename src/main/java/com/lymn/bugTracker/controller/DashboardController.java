package com.lymn.bugTracker.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lymn.bugTracker.dto.UserDto;
import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.FileRepository;
import com.lymn.bugTracker.repository.UserRepository;
import com.lymn.bugTracker.service.DashboardService;

@Controller
public class DashboardController {
	@Autowired
	DashboardService dashboardService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private UserDto userDto;
	@Autowired
	FileRepository fileRepository;
	@Autowired
	BugRepository bugRepository;
	
	
	@RequestMapping(path = "/dashboard")
	public ModelAndView dashboard(Authentication authentication, HttpSession httpSession) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dashboard");
		modelAndView.addObject("countProject");
		modelAndView.addObject("bugCount", dashboardService.bugCount());
		modelAndView.addObject("critical", bugRepository.countBySeverity("critical"));
		modelAndView.addObject("moderate", bugRepository.countBySeverity("moderate"));
		modelAndView.addObject("normal", bugRepository.countBySeverity("normal"));
		modelAndView.addObject("downloadable", fileRepository.count());
		modelAndView.addObject("projectCount", dashboardService.projectCount());
		modelAndView.addObject("title","Dashboard|BugTracker");
		if(httpSession.getAttribute("role")==null) {
			httpSession = userDto.primaryInit(authentication, httpSession);	
		}
		return modelAndView;
	}

}
