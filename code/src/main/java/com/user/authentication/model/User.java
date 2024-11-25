package com.user.authentication.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_user", schema = "dbo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@Column(name = "user_id_pk", columnDefinition = "VARCHAR(36)")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String userIdPk;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_department")
	private String userDepartment;

	@Column(name = "user_password")
	private String userPassword;

	@Column(name = "user_role")
	private String userRole;

	@Column(name = "user_id")
	private String userId;

//	@Column(name = "default_password")
//	private String defaultPassword;

	@Column(name = "last_login")
	private LocalDateTime lastLoginDate;

	@Column(name = "last_password_updated")
	private LocalDateTime lastPasswordUpdatedDate;

	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	@Column(name = "reset_password_token_creation")
	private LocalDateTime resetPasswordTokenCreation;

	@Column(name = "status")
	private String status;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_on")
	private LocalDateTime updatedOn;
	
	@Column(name = "is_email_sent")
	private Boolean isEmailSent;

}
