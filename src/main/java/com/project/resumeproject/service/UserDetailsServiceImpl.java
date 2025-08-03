package com.project.resumeproject.service;

import org.springframework.stereotype.Component;

import com.project.resumeproject.entity.Users;
import com.project.resumeproject.repository.Userrrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private Userrrepository userrepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users userDetails=userrepository.findByusername(username);
		
		if(userDetails!=null)
		{
			
			return org.springframework.security.core.userdetails.User.builder().username(userDetails.getUsername())
			.password(userDetails.getPassword())
			  .roles("USER")
			.build();
			
		}
		throw new UsernameNotFoundException("User not found with username: "+username);
		
		
	}
	
	
	

}
