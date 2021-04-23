package com.lymn.bugTracker.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lymn.bugTracker.model.Message;
import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.MessageRepository;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.repository.UserRepository;

@Controller
public class MessageController {

	@Autowired
	MessageRepository messageRepository;
	@Autowired
	BugRepository bugRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ModifiedRepository modifiedRepository;

	@RequestMapping("bug/{id}/message/save")
	public String saveMessage(@PathVariable("id") Long id,@RequestParam("description") String description, Model model, HttpSession httpSession) {
		
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			Message message = new Message();
			message.setBug(bugRepository.findById(id).get());
			message.setDescription(description);
			message.setUploadTime(new Date());
			message.setUser(userRepository.findById((Long) httpSession.getAttribute("uId")).get());
			messageRepository.save(message);
			return "redirect:/bugs/"+id+"/view";
		}else {
			if(modifiedRepository.bugIsPresent(id, (Long) httpSession.getAttribute("uId"))) {
				Message message = new Message();
				message.setBug(bugRepository.findById(id).get());
				message.setDescription(description);
				message.setUploadTime(new Date());
				message.setUser(userRepository.findById((Long) httpSession.getAttribute("uId")).get());
				messageRepository.save(message);
				return "redirect:/bugs/"+id+"/view";
			}
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
}
