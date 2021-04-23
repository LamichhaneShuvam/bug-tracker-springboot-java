package com.lymn.bugTracker.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lymn.bugTracker.dto.ProjectViewDto;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.ProjectRepository;
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
	@Autowired
	ProjectRepository projectRepository;
	
	@RequestMapping("project/{id}/assigntask")
	public String giveProjectAuthority(@PathVariable("id") Long id,Model model, HttpSession httpSession) {
		model.addAttribute("title","Assign Project");
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("project",projectService.getProjectById(id));
			List<ProjectViewDto> assignedUsers = modifiedRepository.findProjectViewerByAuthority(id);
			List<ProjectViewDto> allUsers = modifiedRepository.remainingUser();
			List<ProjectViewDto> remainingUsers = allUsers;
			for(ProjectViewDto assigned : assignedUsers) {
					remainingUsers.removeIf(user -> user.getId()==assigned.getId());
			}
			model.addAttribute("assignedUserList", modifiedRepository.findProjectViewerByAuthority(id));
			model.addAttribute("remainingUser", remainingUsers);//modifiedRepository.remainingUser());
			model.addAttribute("project", projectRepository.findById(id).get());
			model.addAttribute("users","");
			return "assign_project";
		}else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}

	@RequestMapping("project/{id}/assign/{uId}")
	public String saveProjectAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		if(modifiedRepository.projectIsPresent(id, userId)) {
			model.addAttribute("title", "Assigned");
			return "alreadyassigned";
		}
		modifiedRepository.saveProjectAuthority(id, userId);
		return "redirect:/project/"+id+"/assigntask";
	}
	
	@RequestMapping("project/{id}/remove/{uId}")
	public String removeProjectAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		modifiedRepository.removeProjectAuthority(id, userId);
		return "redirect:/project/"+id+"/assigntask";
	}
	
	@RequestMapping("bug/{id}/assignbug")
	public String giveBugAuthority(@PathVariable("id") Long id ,Model model, HttpSession httpSession) {
		model.addAttribute("title","Assign Bug");
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			model.addAttribute("project" ,modifiedRepository.findProjectByBugId(id));
			model.addAttribute("bug",bugService.getBugById(id));
			model.addAttribute("assignedUserList", modifiedRepository.findBugViewerByAuthority(id));
			List<ProjectViewDto> assignedUsers = modifiedRepository.findBugViewerByAuthority(id);
			List<ProjectViewDto> allUsers = modifiedRepository.remainingUser();
			List<ProjectViewDto> remainingUsers = allUsers;
			for(ProjectViewDto assigned : assignedUsers) {
				remainingUsers.removeIf(user -> user.getId()==assigned.getId());
			}
			model.addAttribute("remainingUser", remainingUsers);
			return "assign_bug";
		}else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	
	@RequestMapping("bug/{id}/assign/{uId}")
	public String saveBugAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		//here do find project bug bug id...
		model.addAttribute("project" ,modifiedRepository.findProjectByBugId(id));
		//do the validation for project authority not given or give the authority at this point
		if(modifiedRepository.bugIsPresent(id, userId)) {
			model.addAttribute("title","Assigned");
			return "alreadyassigned";
		}
		modifiedRepository.saveBugAuthority(id, userId);
		return "redirect:/bug/"+id+"/assignbug";
	}

	@RequestMapping("bug/{id}/remove/{uId}")
	public String removeBugAuthority(@PathVariable("id") Long id, @PathVariable("uId") Long userId, Model model) {
		model.addAttribute("project" ,modifiedRepository.findProjectByBugId(id));
		modifiedRepository.removeBugAuthority(id, userId);
		return "redirect:/bug/"+id+"/assignbug";
	}
}
