package com.lymn.bugTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.lymn.bugTracker.model.User;
import com.lymn.bugTracker.repository.UserRepository;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/register")
	public String showRegister(Model model) {
		model.addAttribute("user", new User()); 
		return "register_form";
	}
	
	@PostMapping("/register/process_register")
	public String processRegister(User user) { 
		BCryptPasswordEncoder passencode = new BCryptPasswordEncoder();
		String encodedPass = passencode.encode(user.getPassword());
		user.setPassword(encodedPass);
		userRepository.save(user);
		return "/login";
	}
//	@RequestMapping("/user/{id}")
//	public String getrole(@PathVariable("id")Long id) {
//		User user = userRepository.findById(id).get();
//		for(Role role : user.getRoles()) {
//			System.out.print(role.getName());
//		}
//		return "/";
//	}
	
}
