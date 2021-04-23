package com.lymn.bugTracker.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lymn.bugTracker.model.User;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.UserRepository;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ModifiedRepository modifiedRepository;
	
	@GetMapping("/register")
	public String showRegister(Model model) {
		model.addAttribute("user", new User());	
		model.addAttribute("title","Register User");
		return "register_form";
	}
	
	@PostMapping("/register/process_register")
	public String processRegister(User user) { 
		BCryptPasswordEncoder passencode = new BCryptPasswordEncoder();
		String encodedPass = passencode.encode(user.getPassword());
		user.setPassword(encodedPass);
		userRepository.save(user);
		return "redirect:/users";
	}
	
	@RequestMapping("/users")
	public String showUsers(Model model, HttpSession httpSession) {
		model.addAttribute("title", "Users");
		if(httpSession.getAttribute("role").equals("ADMIN")) {
			model.addAttribute("users", modifiedRepository.viewEditUserRoles());	
			return "users";
		}
		model.addAttribute("title", "Unauthorized");
		return "403";
	}
	@RequestMapping("/user/{id}/role")
	public String showRoleChange(@PathVariable("id") Long id, Model model, HttpSession httpSession) {
		model.addAttribute("title", "Role Manager");
		if(httpSession.getAttribute("role").equals("ADMIN")) {
			model.addAttribute("users", modifiedRepository.viewUserByRole());
			model.addAttribute("userId",id);
			return "role";
		}
		model.addAttribute("title", "Unauthorized");
		return "403";
	}
	
	@RequestMapping("/user/{id}/role/save")
	public String updateRole(@PathVariable("id")Long id,@RequestParam("role") String role) {
		if(!modifiedRepository.userRolePresent(id)) {
			switch (role) {
			case "ADMIN":
				modifiedRepository.UpdateRole(id, 1l);
				break;
			case "MANAGER":
				modifiedRepository.UpdateRole(id, 2l);
				break;
			case "DEVELOPER":
				modifiedRepository.UpdateRole(id, 3l);
				break;
			case "TESTER":
				modifiedRepository.UpdateRole(id, 4l);
				break;
			default:
				break;
			}			
		} else {
			switch (role) {
			case "ADMIN":
				modifiedRepository.AssignRole(id, 1l);
				break;
			case "MANAGER":
				modifiedRepository.AssignRole(id, 2l);
				break;
			case "DEVELOPER":
				modifiedRepository.AssignRole(id, 3l);
				break;
			case "TESTER":
				modifiedRepository.AssignRole(id, 4l);
				break;
			default:
				break;
			}			
			
		}
		return "redirect:/users";
	}
}
