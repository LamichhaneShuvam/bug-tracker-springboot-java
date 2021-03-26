package com.lymn.bugTracker.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.service.BugService;
import com.lymn.bugTracker.service.ProjectService;

@Controller
public class TaskAssignController {
	@Autowired
	ModifiedRepository modifiedRepository;
	@Autowired
	ProjectService projectService;
	@Autowired
	BugService bugService;
	
	@RequestMapping("project/{id}/assigntask")
	public String giveProjectAuthority(@PathVariable("id") Long id,Model model, HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("project",projectService.getProjectById(id));
			model.addAttribute("assignedUserList", modifiedRepository.findProjectViewerByAuthority(id));
			model.addAttribute("remainingUser", modifiedRepository.remainingUser());
			model.addAttribute("users","");
			return "assign_project";
		}else {
			return "403";
		}
	}
	@RequestMapping("project/{id}/assign/{uId}")
	public String saveProjectAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		if(modifiedRepository.projectIsPresent(id, userId)) {
			return "alreadyassigned";
		}
		modifiedRepository.saveProjectAuthority(id, userId);
		return "redirect:/project/"+id+"/assigntask";
	}
	
	@RequestMapping("bug/{id}/assignbug")
	public String giveBugAuthority(@PathVariable("id") Long id ,Model model, HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("project" ,modifiedRepository.findProjectByBugId(id));
			System.out.println(id);
			model.addAttribute("bug",bugService.getBugById(id));
			model.addAttribute("assignedUserList", modifiedRepository.findBugViewerByAuthority(id));
			model.addAttribute("remainingUser", modifiedRepository.remainingUser());
			return "assign_bug";
		}else {
			return "403";
		}
	}
	
	@RequestMapping("bug/{id}/assign/{uId}")
	public String saveBugAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		//here do find project bug bug id...
		model.addAttribute("project" ,modifiedRepository.findProjectByBugId(id));
		//do the validation for project authority not given or give the authority at this point
		if(modifiedRepository.bugIsPresent(id, userId)) {
			return "alreadyassigned";
		}
		modifiedRepository.saveBugAuthority(id, userId);
		return "redirect:/bug/"+id+"/assignbug";
	}
}
