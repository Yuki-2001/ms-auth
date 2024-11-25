/**
 * 
 */
package com.user.authentication.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author srithar_r
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetReqDto {

	@Email (message = "{resetReqDto.userEmail.email}")
	@NotBlank (message = "{resetReqDto.userEmail.NotBlank}")
	private String userEmail;
	
	@NotBlank (message = "{resetReqDto.userPassword.NotBlank}")
	private String userPassword;
	
	@NotBlank (message = "{resetReqDto.token.NotBlank}")
	private String token;
}
