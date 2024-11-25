package com.user.authentication.validator;

import java.util.Map;

import com.est.infra.utils.proxy.TypesProxy;
import com.user.authentication.dto.UserReqDto;

public class UserValidator {

	public static boolean validate(UserReqDto userReqDtoClean, Map<String, String> errors) {
		boolean errorsFlag = false;

		if (TypesProxy.isBlankOrNull(userReqDtoClean.getUserEmail())) {
			errors.put("userEmail", "User Email Should not be Empty");
			errorsFlag = true;
		}

		if (TypesProxy.isBlankOrNull(userReqDtoClean.getUserId())) {
			errors.put("userId", "User Id Should not be Empty");
			errorsFlag = true;
		}

		if (TypesProxy.isBlankOrNull(userReqDtoClean.getUserName())) {
			errors.put("userName", "User Name Should not be Empty");
			errorsFlag = true;
		}
		
		if (TypesProxy.isBlankOrNull(userReqDtoClean.getUserDepartment())) {
			errors.put("userDepartment", "User Department Should not be Empty");
			errorsFlag = true;
		}
		return errorsFlag;
	}

}
