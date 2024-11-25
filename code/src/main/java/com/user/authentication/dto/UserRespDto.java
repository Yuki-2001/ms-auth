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
public class UserRespDto {
	
	private Long userId;
	private String userName;
	private String userEmail;
	private String userRole;
	private String userStatus;
	
}
