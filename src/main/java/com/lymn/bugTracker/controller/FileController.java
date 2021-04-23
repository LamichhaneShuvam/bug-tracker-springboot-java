package com.lymn.bugTracker.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lymn.bugTracker.model.File;
import com.lymn.bugTracker.model.Message;
import com.lymn.bugTracker.repository.BugRepository;
import com.lymn.bugTracker.repository.FileRepository;
import com.lymn.bugTracker.repository.MessageRepository;
import com.lymn.bugTracker.repository.ModifiedRepository;
import com.lymn.bugTracker.service.BugService;

@Controller
public class FileController {
	@Autowired
	FileRepository fileRepository;
	
	@Autowired
	BugRepository bugRepository;
	
	@Autowired
	ModifiedRepository modifiedRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	BugService bugService;
	
	@RequestMapping("/bugs/{id}/view")
	public String view(@PathVariable(name="id")Long id, Model model, HttpSession httpSession) {
		model.addAttribute("title","View Bug Detail");
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			List<File> listFiles = fileRepository.findByBugId(id);
			model.addAttribute("listFiles",listFiles);
			model.addAttribute("bugId",id);
			model.addAttribute("bug",bugRepository.findById(id).get());
			List<Message> messages = messageRepository.findByBugId(id);			
			model.addAttribute("messages", messages);
			return "viewbugdetail";
			
		}else if(bugService.isPresent(id , (Long)httpSession.getAttribute("uId"))) {
			List<File> listFiles = fileRepository.findByBugId(id);
			model.addAttribute("listFiles",listFiles);
			model.addAttribute("bugId",id);
			model.addAttribute("bug",bugRepository.findById(id).get());
			List<Message> messages = messageRepository.findByBugId(id);
			model.addAttribute("messages", messages);
			return "viewbugdetail";
		}
		else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	
	
	
	@PostMapping(path = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadFile(@PathVariable(name="id") Long id,
			@RequestParam(value = "file", required = false) MultipartFile multipartFile, 
			RedirectAttributes redirectAttributes,
			@RequestParam("description") String description, HttpSession httpSession, Model model) throws IOException {
		
		if(httpSession.getAttribute("role").equals("ADMIN") || httpSession.getAttribute("role").equals("MANAGER")) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			File file = new File();
			file.setName(fileName);
			file.setDescription(description);
			file.setContent(multipartFile.getBytes());
			file.setBug(bugRepository.findById(id).get());
			file.setSize(multipartFile.getSize());
			file.setUploadTime(new Date());
			fileRepository.save(file);
			redirectAttributes.addFlashAttribute("message","the file has been successfully uploaded");
			return "redirect:/bugs/"+id+"/view";
		} else if(bugService.isPresent(id , (Long)httpSession.getAttribute("uId"))){
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			File file = new File();
			file.setName(fileName);
			file.setDescription(description);
			file.setContent(multipartFile.getBytes());
			file.setBug(bugRepository.findById(id).get());
			file.setSize(multipartFile.getSize());
			file.setUploadTime(new Date());
			fileRepository.save(file);
			redirectAttributes.addFlashAttribute("message","the file has been successfully uploaded");
			return "redirect:/bugs/"+id+"/view";
		}else {
			model.addAttribute("title", "Unauthorized");
			return "403";
		}
	}
	
	@GetMapping(path="/download")
	public void downloadFile(@Param("id") Long id, HttpServletResponse httpServletResponse) throws Exception {
		Optional<File> result = fileRepository.findById(id);
		if(!result.isPresent()) {
			throw new Exception("File not found with id "+id);			
		}
		File file = result.get();
		httpServletResponse.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename="+file.getName();
		
		httpServletResponse.setHeader(headerKey, headerValue);
		ServletOutputStream outputStream = httpServletResponse.getOutputStream();
		
		outputStream.write(file.getContent());
		outputStream.close();
	}
	
	@GetMapping("/file/display/{id}")
	@ResponseBody
	void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<File> file)
			throws ServletException, IOException {
		List<String> allowedFiles = Arrays.asList(
				"jpeg","jpg","png","gif");
		file = fileRepository.findById(id);
		String[] arr = file.get().getName().split("\\.");
		String fileType = arr[1];
		if(allowedFiles.contains(fileType)) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(file.get().getContent());
			response.getOutputStream().close();
		}else {
			byte[] contentBlob = modifiedRepository.findImage(1l);
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(contentBlob);
			response.getOutputStream().close();
			
		}
	}
}
