package com.usermanagement.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.usermanagement.bindings.ActivateAccount;
import com.usermanagement.bindings.UserDetails;
import com.usermanagement.bindings.UserLogin;
import com.usermanagement.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> userRegistration(@RequestBody UserDetails userDetails) {
		String msg = userService.createUser(userDetails);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	@PostMapping("/activate")
	public ResponseEntity<String> activateAccount(@RequestBody ActivateAccount activateAccount) {
		boolean isActivated = userService.activateUser(activateAccount);
		if (isActivated)
			return new ResponseEntity<String>("Account Activated", HttpStatus.OK);
		else
			return new ResponseEntity<String>("Invalid Credentials", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDetails>> getAllUsers() {
		return new ResponseEntity<List<UserDetails>>(userService.getAllUsers(), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<UserDetails> getUserById(@PathVariable Long userId) {
		return new ResponseEntity<UserDetails>(userService.getUserById(userId), HttpStatus.OK);
	}

	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		boolean isDeleted = userService.deleteUser(userId);
		if (isDeleted)
			return new ResponseEntity<String>("Deleted", HttpStatus.OK);
		else
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/status/{userId}/{status}")
	public ResponseEntity<String> changeStatus(@PathVariable Long userId, @PathVariable String status) {
		boolean isChanged = userService.updateAccountStatus(userId, status);
		if (isChanged)
			return new ResponseEntity<String>("Status Changed", HttpStatus.OK);
		else
			return new ResponseEntity<String>("Status Failed To Change", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLogin userLogin) {
		String status = userService.login(userLogin);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}

	@GetMapping("/resetPwd/{email}")
	public ResponseEntity<String> resetPassword(@PathVariable String email) {
		String status = userService.recoverPassword(email);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateUser(@RequestBody UserDetails userDetails) {
		boolean updateUser = userService.updateUser(userDetails);
		if (updateUser)
			return new ResponseEntity<String>("Updated", HttpStatus.OK);
		else
			return new ResponseEntity<String>("Update Failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
