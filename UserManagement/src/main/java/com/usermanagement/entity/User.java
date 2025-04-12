package com.usermanagement.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USER")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	private String fullName;
	private String email;
	private String mobile;
	private String password;
	private String status;
	private LocalDate dateOfBirth;
	private String gender;
	private Long ssn;
	@CreationTimestamp
	@Column(name = "CREATION_DATE", updatable = false)
	private LocalDate creationDate;
	@CreationTimestamp
	@Column(name = "LAST_UPDATED_DATE", insertable = false)
	private LocalDate lastUpdatedDate;
	private String createdBy;
	private String lastUpdatedBy;

}
