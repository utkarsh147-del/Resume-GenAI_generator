package com.project.resumeproject.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "users")

@Getter
@Setter
@Data
public class Users {
	
	@Id 
	private ObjectId Id;
	@Indexed(unique=true)
	@NonNull
	private String username;
	@NonNull
	private String password;
	
	private String totpSecret;
	
	private boolean is2FAEnabled;
	private boolean is2FAVerified;
	private List<Resume> resumecollection;
	
	private Resume resumeData;

}
