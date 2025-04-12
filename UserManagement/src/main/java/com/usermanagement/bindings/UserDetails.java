package com.usermanagement.bindings;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserDetails {

	private String fullName;
	private String email;
	private String mobile;
	private String gender;
	private LocalDate dateOfBirth;
	private Long ssn;
}
