package com.project.resumeproject.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Document(collection="resume_make")
@Getter
@Setter
@Data
public class Resume {
	
	
	@Id 
	private ObjectId Id;
			private String resumename;
			private String basicInfo;
			private String description;
	        private String skills;
	        private String experience;
	        private String education;
	        private String projects;
	        private String generatedResume; 
	        private String content;
	        
	       

}
