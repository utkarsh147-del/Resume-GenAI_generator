package com.project.resumeproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.project.resumeproject.entity.Users;
import com.project.resumeproject.repository.Userrrepository;

@Service
public class UserService {
	
@Autowired
private Userrrepository userrepository;

private static final BCryptPasswordEncoder passwordencoder=new BCryptPasswordEncoder();

public boolean saveNewEntry(Users user)
{
	try
	{
	user.setPassword(passwordencoder.encode(user.getPassword()));
	user.set2FAEnabled(false);
	userrepository.save(user);
	return true;
	}
	catch(Exception e)
	{

		return false;
	}
	
}

public void saveUser(Users user)
{

	userrepository.save(user);
}

public Users findByUserName(String username)
{
	return userrepository.findByusername(username);
}


}
