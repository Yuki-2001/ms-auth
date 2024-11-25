package com.user.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author srithar_r
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {

	private String userName;
	private String userDepartment;
	private String userEmail;
//	private String userPassword;
	private String userId;
}
