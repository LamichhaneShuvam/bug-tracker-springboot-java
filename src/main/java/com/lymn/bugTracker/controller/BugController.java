package com.lymn.bugTracker.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lymn.bugTracker.dto.UserDto;
import com.lymn.bugTracker.model.Bug;
import com.lymn.bugTracker.model.Project;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.service.BugService;
import com.lymn.bugTracker.service.ProjectService;

@Controller
public class BugController {
	@Autowired
	BugService bugService;
	@Autowired 
	ProjectService projectService;
	@Autowired
	ModifiedRepository modifiedRepository;
	@Autowired
	UserDto userDto;
	
	@RequestMapping(path = "/project/{id}/bugs")
	public String viewBugs(Model model, @PathVariable(name="id") Long id,Authentication authentication, HttpSession httpSession) {
		model.addAttribute("title","Bugs"); //add project's name if needed
		model.addAttribute("projectId", id); //this is for references
		if(httpSession.getAttribute("role") == null) {
			httpSession = userDto.primaryInit(authentication, httpSession);
		}	
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("bugList", bugService.getBugByProjectId(id));
			model.addAttribute("project",projectService.getProjectById(id));
			return "bug";
		}
		else if(projectService.isPresent(id , (Long)httpSession.getAttribute("uId"), (String)httpSession.getAttribute("role"))){
				model.addAttribute("bugList", modifiedRepository.findBugByUserId(id, (Long)httpSession.getAttribute("uId")));
				model.addAttribute("project",projectService.getProjectById(id));
				return "bug";	
		}
		else {
			return "403";
		}

	}
	
	
	@RequestMapping(path = "/project/{id}/bugs/add")
	public String newBug(@PathVariable(name="id")Long id,Model model, HttpSession httpSession, Authentication authentication) {
		if(httpSession.getAttribute("role") == null) {
			httpSession = userDto.primaryInit(authentication, httpSession);
		}
		if(projectService.isPresent(id , (Long)httpSession.getAttribute("uId"), (String)httpSession.getAttribute("role"))){
			System.out.println("hello 1");
			model.addAttribute("project",projectService.getProjectById(id));
			model.addAttribute("projectId", id);
			model.addAttribute("title","New Bug");
			return "new_bug";
		}else {
			return "403";
		}
	}
	
	@RequestMapping(path="/project/{id}/bugs/save")
	public String saveBug(@PathVariable(name="id") Long id,Model model, Bug bug,HttpSession httpSession) {
		List<String> valid = Arrays.asList(
					"normal", "moderate", "critical"
				);
		if(valid.contains(bug.getSeverity())) {
			if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
				Project project = projectService.getProjectById(id);
				bug.setProject(project);
				bugService.save(bug);
				return "redirect:/project/"+id+"/bugs";
			}//combine if and else if to one using " ->or<- "
			else if(projectService.isPresent(id , (Long)httpSession.getAttribute("uId"), (String)httpSession.getAttribute("role"))){
				Project project = projectService.getProjectById(id);
				bug.setProject(project);
				bugService.save(bug);
				return "redirect:/project/"+id+"/bugs";
			}else {
				return "403";
			}
		} else {
			return "400";
		}
		
	}
}
