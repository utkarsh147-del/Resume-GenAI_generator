package com.project.resumeproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.resumeproject.entity.Users;
import com.project.resumeproject.repository.Userrrepository;
import com.project.resumeproject.service.UserService;
import com.project.resumeproject.utils.JWTutils;

@RestController


@RequestMapping("/public")

public class PublicController {
	
	@Autowired
	private UserService userservice;
	
	@Autowired
	private Userrrepository userrepository;
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
	private JWTutils jwt;
	
	@GetMapping("/health-check")
	public String health_check()
	{
		System.out.println("OK");
		return "OK";
	}
	@PostMapping("/signup")
	public ResponseEntity<String>  signup(@RequestBody Users user)
	{
		userservice.saveNewEntry(user);
		 return ResponseEntity.ok("User registered with ID: " + user.getId());
	}
	
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
		try
		{
			System.out.println("en0");	
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        
        System.out.println("en1");
        String jwts = jwt.generateToken(user.getUsername());
        
        System.out.println("en3");
        
     Users db=userservice.findByUserName(user.getUsername());
     System.out.println("en4");
     System.out.println(db.is2FAEnabled());
     System.out.println(db.is2FAVerified());
     Map<String,Object> response=new HashMap<>();
     if(db.is2FAEnabled()&&!db.is2FAVerified())
     {
    	 
    	 response.put("status", "2FA_REQUIRED");
    	    response.put("message", "2FA required. Use /user/verify-2fa with TOTP code.");
    	    response.put("token", jwts);
    	 
    	    
     }
     else 
     {
     response.put("status", "2FA_NOTENABLED");
     response.put("token", jwts);
     }
        return ResponseEntity.ok(response);
    }
		catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

}
}