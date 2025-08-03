package com.project.resumeproject.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.resumeproject.entity.Resume;
import com.project.resumeproject.entity.Users;

public interface Resumerepository extends MongoRepository<Resume, ObjectId>  {
	


}
