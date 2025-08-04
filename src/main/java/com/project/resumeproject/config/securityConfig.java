package com.project.resumeproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.resumeproject.service.UserDetailsServiceImpl;
import com.project.resumeproject.utils.JWTutils;
import com.project.resumeproject.utils.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class securityConfig {
	@Autowired
	public UserDetailsServiceImpl userdetailservice;
	@Autowired
	private JWTutils jwt;
	@Bean
	public PasswordEncoder passwordEncoder() 
	{
		return new BCryptPasswordEncoder();
	}	
		@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http.authorizeRequests().requestMatchers("/user/**").authenticated()
			.requestMatchers("/admin/**").hasRole("ADMIN")
			   .requestMatchers(HttpMethod.OPTIONS,"/public/**").permitAll()
			.anyRequest()
			.permitAll()
			.and()
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		
		 
			
			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().csrf().disable();
	
			  return http.build();
			  
			 

}
		 @Bean
		 public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailService)
		         throws Exception {
			 System.out.println("LLLLL");
		     return http.getSharedObject(AuthenticationManagerBuilder.class)
		             .userDetailsService(userDetailService)
		             .and()
		             .build();
		 }
		 @Bean
	      public JwtAuthenticationFilter jwtAuthenticationFilter() {
	          return new JwtAuthenticationFilter(userdetailservice, jwt);
	      }
		
		

}
