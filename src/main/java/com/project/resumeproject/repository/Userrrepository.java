package com.project.resumeproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.resumeproject.entity.Users;

public interface Userrrepository extends MongoRepository<Users, String>  {
	
	Users findByusername(String username);

	
	

}
