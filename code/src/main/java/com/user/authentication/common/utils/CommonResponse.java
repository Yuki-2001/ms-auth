package com.user.authentication.common.utils;

import java.util.Map;

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
public class CommonResponse {

	private String message;
	private Integer httpStatusCode;
	private String httpStatus;
	private Map<String, ?> validationMessage;

}
