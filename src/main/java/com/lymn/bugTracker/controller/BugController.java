package com.lymn.bugTracker.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lymn.bugTracker.dto.UserDto;
import com.lymn.bugTracker.model.Bug;
import com.lymn.bugTracker.model.Project;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.UserRepository;
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
	UserRepository userRepository;
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
			model.addAttribute("title", "Unauthorized");
			return "403";
		}

	}
	@RequestMapping(path = "/completed/bugs")
	public String viewCompletedBugs(Model model,Authentication authentication, HttpSession httpSession) {
		model.addAttribute("title","Completed Bugs");
		if(httpSession.getAttribute("role") == null) {
			httpSession = userDto.primaryInit(authentication, httpSession);
		}	
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("bugList", modifiedRepository.findBugByCompletedStatus());
			return "complete_bug";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}

	}
	
	
	@RequestMapping(path = "/project/{id}/bugs/add")
	public String newBug(@PathVariable(name="id")Long id,Model model, HttpSession httpSession, Authentication authentication) {
		if(httpSession.getAttribute("role") == null) {
			httpSession = userDto.primaryInit(authentication, httpSession);
		}
		if(projectService.isPresent(id , (Long)httpSession.getAttribute("uId"), (String)httpSession.getAttribute("role"))){
			//System.out.println("hello 1");
			model.addAttribute("project",projectService.getProjectById(id));
			model.addAttribute("projectId", id);
			model.addAttribute("title","New Bug");
			return "new_bug";
		}else {
			model.addAttribute("title", "Unauthorized");
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
				bug.setReportTime(new Date());
				bug.setStatus("ongoing");
				//who set the code
				bug.setUser(userRepository.findById((Long) httpSession.getAttribute("uId")).get());
				bugService.save(bug);
				return "redirect:/project/"+id+"/bugs";
			}//combine if and else if to one using " ->or<- "
			else if(projectService.isPresent(id , (Long)httpSession.getAttribute("uId"), (String)httpSession.getAttribute("role"))){
				Project project = projectService.getProjectById(id);
				bug.setProject(project);
				bug.setReportTime(new Date());
				bug.setStatus("ongoing");
				//who set the code
				bug.setUser(userRepository.findById((Long) httpSession.getAttribute("uId")).get());
				bugService.save(bug);
				return "redirect:/project/"+id+"/bugs";
			}
		}
			model.addAttribute("title", "Invalid Request");
			return "400";
		
	}
	@RequestMapping("project/{pid}/bug/{id}/complete")
	public String bugCompleted(@PathVariable(name="id")Long bugId,@PathVariable(name="pid") Long projectId
								, Model model,HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.bugStatus(bugId, "completed");
			return "redirect:/project/"+projectId+"/bugs";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	@RequestMapping("project/{pid}/bug/{id}/ongoing")
	public String bugOngoing(@PathVariable(name="id")Long bugId,@PathVariable(name="pid") Long projectId
								, Model model,HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.bugStatus(bugId, "ongoing");
			return "redirect:/project/"+projectId+"/bugs";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	@RequestMapping("project/{pid}/bug/{id}/edit")
	public String bugEdit(@PathVariable(name="id")Long bugId,@PathVariable(name="pid") Long projectId
								, Model model,HttpSession httpSession) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("projectId", projectId);
			model.addAttribute("bugId",bugId);
			return "edit_bug";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	@RequestMapping("project/{pid}/bug/{id}/edit/save")
	public String bugEditSave(@PathVariable(name="id")Long bugId,@PathVariable(name="pid") Long projectId
								, Model model,HttpSession httpSession
								, @RequestParam("name") String name, @RequestParam("description")String description
								, @RequestParam("severity")String severity) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.editBug(bugId, name, description, severity);
			
			return "redirect:/project/"+projectId+"/bugs";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	@RequestMapping("project/{pid}/bug/{id}/delete")
	public String bugDelete(@PathVariable(name = "pid") Long projectId, @PathVariable(name="id") Long bugId, HttpSession httpSession, Model model) {
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			modifiedRepository.bugDelete(bugId);
			return "redirect:/project/"+projectId+"/bugs";
		}
		model.addAttribute("title","Unauthorized");
		return "403";
	}
	
}
