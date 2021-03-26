package com.lymn.bugTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
	@RequestMapping("/unauthorized")
	public String unauthorizedAccess() {
		return "redirect:/403";	
	}
}
