package com.lymn.bugTracker.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lymn.bugTracker.dto.UserDto;
import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.FileRepository;
import com.lymn.bugTracker.repository.ModifiedRepository;
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
	@Autowired
	ModifiedRepository modifiedRepository;
	
	
	@RequestMapping(path="/")
	public String redirectEmpty() {
		return "redirect:/dashboard";
	}
	@RequestMapping(path="/index")
	public String redirectIndex() {
		return "redirect:/dashboard";
	}
	
	@RequestMapping(path = "/dashboard")
	public ModelAndView dashboard(Authentication authentication, HttpSession httpSession) {
		if(httpSession.getAttribute("role")==null) {
			httpSession = userDto.primaryInit(authentication, httpSession);	
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dashboard");
		modelAndView.addObject("countProject");
		modelAndView.addObject("bugCount", dashboardService.bugCount());
		modelAndView.addObject("critical", bugRepository.countBySeverity("critical"));
		modelAndView.addObject("moderate", bugRepository.countBySeverity("moderate"));
		modelAndView.addObject("normal", bugRepository.countBySeverity("normal"));
		modelAndView.addObject("downloadable", fileRepository.count());
		modelAndView.addObject("projectCount", dashboardService.projectCount());
		modelAndView.addObject("messages",dashboardService.messageCount());
		modelAndView.addObject("topreporter", "Shuvam");
		modelAndView.addObject("title","Bug Tracker");

		return modelAndView;
	}
	@RequestMapping(path="/setting")
	public String settings(Model model, Authentication auth) {
		if(auth.isAuthenticated()) {
			return "settings";			
		}
		else {
			model.addAttribute("title","Unauthorized access");
			return "403";
		}
		
	}
	
	@PostMapping(path = "/setting/change/password")
	public String changePassword(Model model, HttpSession httpSession, @RequestParam("oldpass")String oldPassword, @RequestParam("newpass")String newPassword){
		BCryptPasswordEncoder passencode = new BCryptPasswordEncoder();
		if(passencode.matches(oldPassword,modifiedRepository.getOldPassword((Long)httpSession.getAttribute("uId")))) {
			String newEncodedPassword = passencode.encode(newPassword);
			modifiedRepository.setNewPassword((Long)httpSession.getAttribute("uId"), newEncodedPassword);
			return "redirect:/dashboard";
		}else {
			model.addAttribute("err","old password is not correct");
			model.addAttribute("title","password not correct");
			return "customErr";
		}
	}
	
	@PostMapping(path = "/setting/change/email")
	public String changeUsername(Model model, HttpSession httpSession, @RequestParam("email")String email){
		modifiedRepository.setNewEmail((Long)httpSession.getAttribute("uId"), email);
		return "redirect:/dashboard";
	}
}
