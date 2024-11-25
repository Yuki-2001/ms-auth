package com.user.authentication.service;

import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.est.infra.utils.exception.ApplicationException;
import com.user.authentication.common.utils.CommonResponse;
import com.user.authentication.dto.ForgotReqDto;
import com.user.authentication.dto.ResetReqDto;
import com.user.authentication.dto.UserReqDto;

public interface IUserService {

	/**
	 * @param forgotReqDto
	 * @return
	 * @throws ApplicationException
	 */
	Optional<String> forgotPassword(@Valid ForgotReqDto forgotReqDto) throws ApplicationException;

	/**
	 * @param email
	 * @param token
	 * @return
	 * @throws ApplicationException
	 */
	Optional<CommonResponse> validateToken(String email, String token, String redirectUrl) throws ApplicationException;

	/**
	 * @param resetReqDto
	 * @return
	 * @throws ApplicationException
	 */
	Optional<CommonResponse> resetPassword(@Valid ResetReqDto resetReqDto) throws ApplicationException;

	/**
	 * @param userReqDtoClean
	 * @param errors
	 * @return
	 */
	Optional<Boolean> saveUserDetails(UserReqDto userReqDtoClean, Map<String, String> errors)throws ApplicationException;

}
