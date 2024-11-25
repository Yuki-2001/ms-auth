package com.user.authentication.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "custom_employee_details")
public class EmployeeDetails {

	@Id
	@Column(name = "default_id_pk", nullable = false, length = 36)
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String employeeIdPk;

	@Column(name = "custom_employee_id")
	private String employeeId;

	@Column(name = "custom_employee_name")
	private String employeeName;

	@Column(name = "custom_head_department_name")
	private String headDepartmentName;

	@Column(name = "custom_department_name")
	private String departmentName;

	@Column(name = "custom_employee_role")
	private String employeeRole;

	@Column(name = "custom_email_address")
	private String emailAddress;

	@Column(name = "default_created_by", nullable = false, length = 50)
	private String createdBy;

	@Column(name = "default_created_on", nullable = false)
	private LocalDateTime createdOn;

	@Column(name = "default_updated_by", length = 50)
	private String updatedBy;

	@Column(name = "default_updated_on")
	private LocalDateTime updatedOn;

	@Column(name = "default_status", length = 5)
	private String status;

	@Column(name = "default_search_criteria")
	private String searchCriteria;
}
