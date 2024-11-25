/**
 * 
 */
package com.user.authentication.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

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
public class ForgotReqDto {

	@Email (message = "{forgotReqDto.userEmail.email}")
	@NotBlank (message = "{forgotReqDto.userEmail.NotBlank}")
	private String userEmail;
	
	@NotBlank (message = "{forgotReqDto.redirectUrl.NotBlank}")
	@URL (message = "{forgotReqDto.redirectUrl.url}")
	private String redirectUrl;
}
