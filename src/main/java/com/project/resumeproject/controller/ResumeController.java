package com.project.resumeproject.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.resumeproject.entity.Resume;
import com.project.resumeproject.entity.Users;
import com.project.resumeproject.service.ResumeService;
import com.project.resumeproject.service.UserService;

@RestController

@RequestMapping("/resume")
public class ResumeController {
	
	@Autowired
	private UserService userservice;
	
	@Autowired
	private ResumeService resumeservice;
	
	@PostMapping("/generate-resume")
    public ResponseEntity<String> generateResume(@RequestBody Resume request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
       if (user.getResumeData() == null) {
           user.setResumeData(new Resume());
       }
      
        
       System.out.println("first "+request.getId());
    String   generated_resume = resumeservice.generateResume(user, request.getSkills(), request.getExperience(), request.getEducation(),request.getProjects(),request.getDescription(),request.getBasicInfo());
       Resume resumedata = user.getResumeData();
       resumedata.setSkills(request.getSkills());
       resumedata.setExperience(request.getExperience());
       resumedata.setEducation(request.getEducation());
       resumeservice.saveResume(user, resume);
       userservice.saveUser(user);
      //  String generated_resume="ll";
        request.setContent(generated_resume);
        return ResponseEntity.ok(generated_resume);
    }
	
	@GetMapping("/delete-resume")
	public ResponseEntity<String> deleteResume(@RequestParam String resumeName) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
//	    Resume resume = new Resume();
//	    
//	    resume.setResumename(request.getResumename());
//	    resume.setSkills(request.getSkills());
//      resume.setExperience(request.getExperience());
//      resume.setEducation(request.getEducation());
        for (Resume resume : user.getResumecollection()) {
            if (resume.getResumename() != null && resume.getResumename().equals(resumeName)) {
            	System.out.println("pehl "+resume.getId());
            //	 resumeservice.deleteResume(username, resume);
            	 user.getResumecollection().remove(resume);
            	    
         	    userservice.saveUser(user);    
         	    return ResponseEntity.ok("Resume saved successfully");
            }
        }
        
        return ResponseEntity.status(404).body("resume not found");
      
     
	}
	
	@PostMapping("/save-resume")
	public ResponseEntity<String> saveResume(@RequestBody Resume request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
//	    Resume resume = new Resume();
//	    
//	    resume.setResumename(request.getResumename());
//	    resume.setSkills(request.getSkills());
//      resume.setExperience(request.getExperience());
//      resume.setEducation(request.getEducation());
        if (request.getId() == null) {
            request.setId(new ObjectId());
        }
        System.out.println("sec "+request.getId());
      
      resumeservice.saveResume(user, request);
    
	    userservice.saveUser(user);    
	    return ResponseEntity.ok("Resume saved successfully");
	}
	
	@GetMapping("/all")
    public ResponseEntity<?> getAllResumes() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        if(user.getResumecollection()==null)
        {
        	return ResponseEntity.ok("No Resume saved");
        }
        return ResponseEntity.ok(user.getResumecollection());
    }
	
	@GetMapping("/get")
    public ResponseEntity<?> getResume(@RequestParam String resumename) {
		System.out.println("reach1");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        System.out.println("reach");
        for (Resume resume : user.getResumecollection()) {
            if (resume.getResumename() != null && resume.getResumename().equals(resumename)) {
           
            	return ResponseEntity.ok(resume.getContent());
            }
        }
        return ResponseEntity.notFound().build();
    }
	
	@PutMapping("/update")
    public ResponseEntity<String> updateResume(@RequestBody Resume request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        System.out.println("edit1");
        for (Resume resume : user.getResumecollection()) {
        	if (resume.getResumename() != null && resume.getResumename().equals(request.getResumename())) {
            	resume.setContent(request.getContent());
                userservice.saveUser(user);
                return ResponseEntity.ok("Resume updated successfully");
            }
        }
        return ResponseEntity.notFound().build();
    }
	
	

	@GetMapping("/test-api")
    public ResponseEntity<String> testApi() throws IOException, InterruptedException {
        String result = resumeservice.getResult("how are you");
        return ResponseEntity.ok(result);
    }

}
