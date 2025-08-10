package com.project.resumeproject.service;

import java.net.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.bson.types.ObjectId;
import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.project.resumeproject.entity.Resume;
import com.project.resumeproject.entity.Users;
import com.project.resumeproject.repository.Resumerepository;
import com.project.resumeproject.repository.Userrrepository;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;


import io.micrometer.core.ipc.http.HttpSender.Response;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



@Service
public class ResumeService {
	
	@Autowired
	UserService userservice;
	
	@Autowired
Resumerepository resumerepository;
	
	  @Value("${spring.ai.key1}")
	  private String aikey1;
	  
	  
	  @Value("${spring.ai.key2}")
	  private String aikey2;
	 @Value("${spring.ai.key3}")
	  private String aikey3;
	  
	  @Value("${spring.aimodel1}")
	  private String aimodel1;
	  
	  @Value("${spring.aimodel2}")
	  private String aimodel2;

	
	
	 public String generateResume(Users user, String skills, String experience, String education,String project,String description,String basicinfo) {
	        String prompt = "Generate a professional resume based on the following details:\n" +
	                "Name and Basic information: " + basicinfo + "\n" +
	                "Skills: " + skills + "\n" +
	                "Experience: " + experience + "\n" +
	                "Education: " + education + "\n" +
	                "Projects: " + project + "\n" +
	                "Description: " + description + "\n" +
	                "Provide a well-structured resume in text format with a proper margins ad gap and headers in a proper aesthetic way "
	                + "100% ATS Score in well defined format caring about heading bold "
	                + "and underline and content should be properly wrapped way.Remember core should be 100% and just show resume only not any other unnecessary output";
	        System.out.println(prompt);
	        try {
	            
	            HttpClient client = HttpClient.newHttpClient();

	            
	            JSONObject payload = new JSONObject();
	            payload.put("model", aimodel1);
	            
	            JSONArray messages = new JSONArray();
	            JSONObject message = new JSONObject();
	            message.put("role", "user");
	            message.put("content", prompt); 
	            messages.put(message);
	            payload.put("messages", messages);

	            
	            HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
	                .header("Content-Type", "application/json")
	                .header("Authorization", "Bearer "+aikey3)
	                .header("HTTP-Referer", "<YOUR_SITE_URL>") 
	                .header("X-Title", "<YOUR_SITE_NAME>") 
	                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
	                .build();

	           
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            System.out.println("Raw Response: " + response.body()); // Debug raw response

	           
	            JSONObject jsonResponse = new JSONObject(response.body());
	            
	           
	            if (jsonResponse.has("choices")) {
	                JSONArray choices = jsonResponse.getJSONArray("choices");
	                if (!choices.isEmpty()) {
	                    String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
	                    System.out.println("Generated Content: " + content);
	                    return content;
	                } else {
	                    return "Error: No choices found in response";
	                }
	            } else {
	                return "Error: Response does not contain 'choices' key. Raw response: " + jsonResponse.toString();
	            }

	        } catch (Exception e) {
	            System.out.println("Exception: " + e.getMessage()); // Debug exception
	            return "Error: " + e.getMessage();
	        }
	    }
	 
	 public void saveResume(Users user, Resume request) {
	        
//	        Resume resumeData = new Resume(null);
//	        resumeData.setResumename(request.getResumename());
//	        resumeData.setSkills(request.getSkills() != null ? request.getSkills() : "");
//	        resumeData.setExperience(request.getExperience() != null ? request.getExperience() : "");
//	        resumeData.setEducation(request.getEducation() != null ? request.getEducation() : "");
//	        resumeData.setContent(request.getContent());
		 System.out.println("id "+request.getId());
	        if(user.getResumecollection()==null)
	      	  user.setResumecollection(new ArrayList<>());
	  	    user.getResumecollection().add(request); 
	    }
	 
	 
	 @Transactional
	    public boolean deleteResume(String username, Resume resume) {
	        boolean removed = false;
	        ObjectId id=resume.getId();
	        try {
	            Users user = userservice.findByUserName(username);
	            System.out.println("nbb "+id);
	            
	            
	            removed = user.getResumecollection().removeIf(x -> x.getId().equals(id));
	            
	            System.out.println(removed);
	            if (removed) {
	                userservice.saveUser(user);
	                resumerepository.deleteById(id);
	            }
	        } catch (Exception e) {
//	            log.error("Error ",e);
	            throw new RuntimeException("An error occurred while deleting the entry.", e);
	        }
	        return removed;
	    }
	 
	 
	 public String getResult(String que) throws IOException, InterruptedException
	 {
		 String prompt = "Generate a professional resume based on the following details:\n" +
	                "Username: " + "vikas"+ "\n" +
	                "Skills: " + "Java, Spring Boot, MongoDB" + "\n" +
	                "Experience: " + "3 years in web development" + "\n" +
	                "Education: " + "B.Tech in Computer Science" + "\n" +
	                "Provide a well-structured resume in text format and wrap the content in a way that all identtion shuld be in a same dimensions.";
		 try {
	            
	            HttpClient client = HttpClient.newHttpClient();

	            
	            JSONObject payload = new JSONObject();
	            payload.put("model", "cognitivecomputations/dolphin-mistral-24b-venice-edition:free");
	            
	            JSONArray messages = new JSONArray();
	            JSONObject message = new JSONObject();
	            message.put("role", "user");
	            message.put("content", prompt);
	            messages.put(message);
	            payload.put("messages", messages);

	           
	            HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
	                .header("Content-Type", "application/json")
	                .header("Authorization", "Bearer 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")
	                .header("HTTP-Referer", "<YOUR_SITE_URL>") // Optional
	                .header("X-Title", "<YOUR_SITE_NAME>") // Optional
	                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
	                .build();

	            
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


	            JSONObject jsonResponse = new JSONObject(response.body());
	            JSONArray choices = jsonResponse.getJSONArray("choices");
	            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

	             
	            System.out.println(content);
	            return content;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return "NO";
	        }
		
		 
	 }
	 
//	 public String testApiKey() {
//	        try {
//	            Map<String, Object> requestBody = new HashMap<>();
//	            requestBody.put("contents", "Test API connection.");
//	            requestBody.put("model", "gemini-1.5-pro");
//
//	            HttpHeaders headers = new HttpHeaders();
//	            headers.set("Content-Type", "application/json");
//	            headers.set("x-goog-api-key", apiKey);
//
//	            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//	            ResponseEntity<Map> response = restTemplate.postForEntity(apiEndpoint, entity, Map.class);
//	            Map<String, Object> body = response.getBody();
//	            if (body != null && body.containsKey("candidates") && !((Map) ((List) body.get("candidates")).get(0)).isEmpty()) {
//	                return ((Map) ((List) body.get("candidates")).get(0)).get("content").toString().trim();
//	            }
//	            return "Failed to test API.";
//	        } catch (Exception e) {
//	            return "Error: " + e.getMessage();
//	        }
//	    }

}
