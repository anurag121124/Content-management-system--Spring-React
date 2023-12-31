package com.cms.services;

import java.util.List;

import com.cms.payload.ForgotPasswordDto;
import com.cms.payload.UserDto;

public interface UserService {
	
	UserDto registerNewUser(UserDto userDto);
	
	UserDto registerNewAdminUser(UserDto userDto);
	
	UserDto createUser(UserDto user);
	
	UserDto updateeUser(UserDto user, Integer userId);
	
	UserDto getUserById(Integer userId);
	
	List<UserDto> getAllUsers();
	
	void deleteUserById(Integer userId);
	
	UserDto forgotPassword(String username, String newPass);
	
	UserDto forgotPasswordTest2(ForgotPasswordDto forgotPasswordDto);
	

}
