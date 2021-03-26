package com.lymn.bugTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RoleController {

	@RequestMapping("/role")
	public String roleAssign() {
		//show table
		return "role";
	}
	//do post processing
}
