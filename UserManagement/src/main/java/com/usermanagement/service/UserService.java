package com.usermanagement.service;

import java.util.List;

import com.usermanagement.bindings.ActivateAccount;
import com.usermanagement.bindings.UserDetails;
import com.usermanagement.bindings.UserLogin;

public interface UserService {

	public String createUser(UserDetails userDetails);

	public boolean activateUser(ActivateAccount activateAccount);

	public UserDetails getUserById(Long userId);
	
	public UserDetails getUserByEmail(String email);
	
	public boolean isEmailPresent(String email);

	public List<UserDetails> getAllUsers();

	public boolean updateUser(UserDetails userDetails);

	public boolean deleteUser(Long userId);

	public boolean updateAccountStatus(Long userId, String status);

	public String login(UserLogin userLogin);

	public String recoverPassword(String email);

}
