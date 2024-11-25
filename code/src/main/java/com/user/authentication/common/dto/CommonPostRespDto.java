package com.user.authentication.common.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonPostRespDto {

	private String message;
	private Integer httpStatusCode;
	private String httpStatus;
	private Map<String, ?> validationMessage;
	private Object savedResponse;
}


