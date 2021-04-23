package com.lymn.bugTracker.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lymn.bugTracker.dto.UserDto;
import com.lymn.bugTracker.model.Project;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.ProjectRepository;
import com.lymn.bugTracker.repository.UserRepository;
import com.lymn.bugTracker.service.ProjectService;

@Controller
public class ProjectController {
	@Autowired
	ProjectService projectService;
	
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ModifiedRepository modifiedRepository;
	
	@Autowired
	UserDto userDto;
	
	@RequestMapping(path = "/project")
	public ModelAndView project(Authentication authentication, HttpSession httpSession) {
		ModelAndView modelAndView = new ModelAndView();
		if(httpSession.getAttribute("role") == null) {
			httpSession = userDto.primaryInit(authentication, httpSession);
			modelAndView.addObject("uId",httpSession.getAttribute("uId"));
			modelAndView.addObject("role",httpSession.getAttribute("role"));
		}else {
			modelAndView.addObject("role",httpSession.getAttribute("role"));		
			modelAndView.addObject("uId",httpSession.getAttribute("uId"));
		}
		if(httpSession.getAttribute("role").equals("ADMIN")||httpSession.getAttribute("role").equals("MANAGER")) {
			modelAndView.addObject("projectList", projectService.getAllProject());	
		} else {
			modelAndView.addObject("projectList", modifiedRepository.findProjectByUserId((Long) httpSession.getAttribute("uId")));
			//modelAndView.addObject("projectList", projectRepository.findByUserId((Long) httpSession.getAttribute("uId")));
			//modelAndView.addObject("projectList",userRepository.findProjectByUserId((Long) httpSession.getAttribute("uId")));
		}
		modelAndView.addObject("title", "Projects");
		modelAndView.setViewName("project");
		return modelAndView;
	}
	
	@RequestMapping("/project/add")
	public String addProject(Model model, HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN")||httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("title","New Project");
			return "new_project";	
		}else {
			model.addAttribute("title","Unauthorized");
			return "403";
		}
	}
	
	@RequestMapping("/project/save")
	public String newProject(Project project, HttpSession httpSession, Model model) {
		if(httpSession.getAttribute("role").equals("ADMIN")||httpSession.getAttribute("role").equals("MANAGER")) {
			project.setStatus("ongoing");
			projectService.save(project);
			return "redirect:/project";			
		} else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	@RequestMapping("/project/{id}/obsolete")
	public String projectObsolete(@PathVariable(name = "id")Long id, HttpSession httpSession, Model model) {
		if(httpSession.getAttribute("role").equals("ADMIN")||httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.projectStatus(id, "obsolete");
			return "redirect:/project";	
		}else {
			model.addAttribute("title","Unauthorized");
			return "403";
		}
	}
	@RequestMapping("project/{id}/delete")
	public String projectDelete(@PathVariable(name = "id")Long id, HttpSession httpSession, Model model) {
		if(httpSession.getAttribute("role").equals("ADMIN")||httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.projectDelete(id);
			return "redirect:/project";			
		}else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	
}
