package com.usermanagement.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.usermanagement.bindings.ActivateAccount;
import com.usermanagement.bindings.UserDetails;
import com.usermanagement.bindings.UserLogin;
import com.usermanagement.entity.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.utils.EmailUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailUtils emailUtils;

	@Override
	public String createUser(UserDetails userDetails) {
		User user = new User();
		BeanUtils.copyProperties(userDetails, user);
		UserDetails foundUser = null;
		String returnValue = "";
		String password = generateRandomPassword();
		foundUser = getUserByEmail(user.getEmail());
		if (foundUser != null) {
			returnValue = "Email exists. Please try different email.";
		} else {
			user.setPassword(password);
			user.setStatus("Inactive");
			User savedUser = userRepository.save(user);
			if (savedUser != null && savedUser.getUserId() != null) {
				returnValue = "User created successfully.";
			} else {
				returnValue = "User creation failed.";
			}

			emailUtils.sendEmail(savedUser.getEmail(), "Registration Successful",
					readMailBody(savedUser.getFullName(), password, "reg-email-body.txt"));
		}
		return returnValue;
	}

	@Override
	public boolean activateUser(ActivateAccount activateAccount) {
		User user = new User();
		user.setEmail(activateAccount.getEmail());
		user.setPassword(activateAccount.getTempPassword());

		Example<User> of = Example.of(user);

		List<User> findAll = userRepository.findAll(of);

		if (findAll.isEmpty()) {
			return false;
		} else {
			User toActivate = findAll.get(0);
			toActivate.setStatus("Active");
			toActivate.setPassword(activateAccount.getConfirmPassword());
			userRepository.save(toActivate);
			return true;
		}
	}

	@Override
	public UserDetails getUserById(Long userId) {
		Optional<User> byId = userRepository.findById(userId);
		if (byId.isPresent()) {
			UserDetails userDetails = new UserDetails();
			User user = byId.get();
			BeanUtils.copyProperties(user, userDetails);
			return userDetails;
		}
		return null;
	}

	@Override
	public List<UserDetails> getAllUsers() {
		List<User> userList = userRepository.findAll();
		List<UserDetails> userDetails = new ArrayList<>();
		for (User user : userList) {
			UserDetails userDetail = new UserDetails();
			BeanUtils.copyProperties(user, userDetail);
			userDetails.add(userDetail);
		}
		return userDetails;
	}

	@Override
	public boolean updateUser(UserDetails userDetails) {
		User user = new User();
		BeanUtils.copyProperties(userDetails, user);
		User updatedUser = userRepository.save(user);
		if (updatedUser.getUserId() != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean deleteUser(Long userId) {
		try {
			userRepository.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateAccountStatus(Long userId, String status) {
		Optional<User> userToUpdate = userRepository.findById(userId);
		if (userToUpdate.isPresent()) {
			User user = userToUpdate.get();
			user.setStatus(status);
			User updatedUser = userRepository.save(user);
			if (updatedUser.getStatus() != null)
				return true;
			else
				return false;
		}
		return false;
	}

	@Override
	public String login(UserLogin userLogin) {
		String email = userLogin.getEmail();
		String password = userLogin.getPassword();

		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			return "Email and password must not be empty";
		}

		User user = userRepository.findByEmailAndPassword(email, password);

		if (user == null) {
			return "Invalid credentials";
		}

		if ("Active".equals(user.getStatus())) {
			return "Login success.";
		} else {
			return "Account is not activated";
		}
	}

	@Override
	public String recoverPassword(String email) {
		User byEmail = userRepository.findByEmail(email);
		if (byEmail == null) {
			return "Invalid Email";
		} else {
			String newPassword = generateRandomPassword();
			byEmail.setPassword(newPassword);
			userRepository.save(byEmail);
			emailUtils.sendEmail(byEmail.getEmail(), "Reset Password Success",
					readMailBody(byEmail.getFullName(), newPassword, "forgot-pwd-mail-body.txt"));
			return "Forgot password success.";
		}
	}

	@Override
	public UserDetails getUserByEmail(String email) {
		User userByEmail = userRepository.findByEmail(email);
		UserDetails userDetails = new UserDetails();
		BeanUtils.copyProperties(userByEmail, userDetails);
		return userDetails;
	}

	@Override
	public boolean isEmailPresent(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	public static String generateRandomPassword() {
		String charLower = "abcdefghijklmnopqrstuvwxyz";
		String charUpper = charLower.toUpperCase();
		String number = "0123456789";
		String symbol = "!@#$%^&*()-_+=<>?";
		int length = 8;

		String passwordAllow = charLower + charUpper + number + symbol;
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(passwordAllow.length());
			password.append(passwordAllow.charAt(index));
		}

		return password.toString();
	}

	public String readMailBody(String fullName, String pwd, String fileName) {
		String url = "";
		String mailBody = "";
		StringBuilder buffer = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mailBody = buffer.toString();
		mailBody = mailBody.replace("{FULLNAME}", fullName);
		mailBody = mailBody.replace("{TEMP-PWD}", pwd);
		mailBody = mailBody.replace("{URL}", url);
		mailBody = mailBody.replace("{PWD}", pwd);

		return mailBody;
	}
}
