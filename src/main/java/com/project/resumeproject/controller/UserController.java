package com.project.resumeproject.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.resumeproject.entity.Users;
import com.project.resumeproject.repository.Userrrepository;
import com.project.resumeproject.service.TwoFactorAuthservice;
import com.project.resumeproject.service.UserService;
import com.project.resumeproject.utils.JWTutils;



@RestController

@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userservice;
	
	@Autowired
	private Userrrepository userrepository;
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
	private JWTutils jwt;
    @Autowired
    private TwoFactorAuthservice twoFactorAuthService;
    
    @GetMapping(value = "/enable-2fa", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> enable2FA() {
    	
     
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user2 = userservice.findByUserName(username);
       			
        if (user2 == null) {
            return ResponseEntity.status(404).body(null);
            
        }
        String secret = twoFactorAuthService.generateSecret();
        user2.setTotpSecret(secret);
        user2.set2FAEnabled(true);
        user2.set2FAVerified(false); 
        userservice.saveUser(user2); 
        byte[] qrCode = twoFactorAuthService.generateQrCode(secret, user2.getUsername());
        return ResponseEntity.ok(qrCode);
    }
    
    
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody TwoFARequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userservice.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        if (twoFactorAuthService.verifyCode(user.getTotpSecret(), request.getTotpCode())) {
           
            userservice.saveUser(user);
            String jwts = jwt.generateToken(user.getUsername());
            return ResponseEntity.ok(jwts);
        } else {
            return ResponseEntity.status(401).body("Invalid TOTP code");
        }
    }
}
    
    class TwoFARequest {
        private String totpCode;

        public String getTotpCode() { return totpCode; }
        public void setTotpCode(String totpCode) { this.totpCode = totpCode; }
    }
	
	

