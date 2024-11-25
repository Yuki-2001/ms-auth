package com.user.authentication.common.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonGetRespDto {

	private Integer totalRecords;
	private Integer totalUnseenTicket;
	private List<?> data;
	private Map<String, String> validationMessage;

}
