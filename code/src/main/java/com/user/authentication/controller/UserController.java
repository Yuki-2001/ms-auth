package com.user.authentication.controller;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.est.infra.security.web.SanitizerHealper;
import com.est.infra.utils.exception.ApplicationException;
import com.est.infra.utils.exception.LayerException;
import com.est.infra.utils.proxy.TypesProxy;
import com.user.authentication.base.controller.BaseController;
import com.user.authentication.common.dto.CommonPostRespDto;
import com.user.authentication.common.utils.CommonResponse;
import com.user.authentication.dto.ForgotReqDto;
import com.user.authentication.dto.ResetReqDto;
import com.user.authentication.dto.UserReqDto;
import com.user.authentication.service.IUserService;
import com.user.authentication.validator.UserValidator;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	@Operation(summary = "Save user details")
	@PostMapping(value = "")
	public ResponseEntity<?> saveUserDetails(@RequestBody UserReqDto userReqDto) throws ApplicationException {
		// adding logger to check running status
		LOGGER.info("saveUserDetails() Has Started At Controller");
		LOGGER.debug("RequestBody: " + userReqDto.toString());
		ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		try {

			Map<String, String> errors = new HashMap<>();
			// Getting user Infromation
			UserReqDto userReqDtoClean = new UserReqDto();

			userReqDtoClean.setUserEmail(SanitizerHealper.cleanString(userReqDto.getUserEmail()));
			userReqDtoClean.setUserId(SanitizerHealper.cleanString(userReqDto.getUserId()));
			userReqDtoClean.setUserName(SanitizerHealper.cleanString(userReqDto.getUserName()));
			userReqDtoClean.setUserDepartment(SanitizerHealper.cleanString(userReqDto.getUserDepartment()));

			// Checking validation using ProjectDetailsValidator
			if (!UserValidator.validate(userReqDtoClean, errors)) {
				Optional<Boolean> result = userService.saveUserDetails(userReqDtoClean, errors);

				if (result.get()) {
					response = new ResponseEntity<>(new CommonPostRespDto("Success", HttpStatus.OK.value(),
							HttpStatus.OK.getReasonPhrase(), errors, null), HttpStatus.OK);
				} else {
					response = new ResponseEntity<>(new CommonPostRespDto("Failed", HttpStatus.BAD_REQUEST.value(),
							HttpStatus.BAD_REQUEST.getReasonPhrase(), errors, null), HttpStatus.BAD_REQUEST);
				}
			}

		} catch (ApplicationException ae) {
			throw new ApplicationException(LayerException.CONTROLLER, ae.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(LayerException.CONTROLLER, e.getMessage());
		}
		LOGGER.info("saveUserDetails() Has Ended At Controller");
		return response;
	}
	
	/**
	 * @param forgotReqDto
	 * @return
	 * @throws ApplicationException
	 */
	@PostMapping(value = "/password/forgot")
	public ResponseEntity<?> forgetPassword(@RequestBody @Valid ForgotReqDto forgotReqDto) throws ApplicationException {

		LOGGER.info("forgetPassword() has been started.");
		ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		Optional<?> result = Optional.empty();
		try {

			String email = SanitizerHealper.cleanString(forgotReqDto.getUserEmail());
			String redirectUrl = SanitizerHealper.cleanString(forgotReqDto.getRedirectUrl());

			ForgotReqDto dto = new ForgotReqDto(email, redirectUrl);

			result = userService.forgotPassword(dto);
			if (result.isPresent()) {
				result = Optional.of(new CommonResponse("Email Sent Successfully", HttpStatus.OK.value(),
						HttpStatus.OK.getReasonPhrase(), null));
				response = new ResponseEntity<>(result.get(), HttpStatus.OK);
			} else {
				result = Optional.of(new CommonResponse("User Email Not Exists", HttpStatus.BAD_REQUEST.value(),
						HttpStatus.BAD_REQUEST.getReasonPhrase(), null));
				response = new ResponseEntity<>(result.get(), HttpStatus.BAD_REQUEST);
			}

		} catch (ApplicationException ae) {
			throw new ApplicationException(LayerException.CONTROLLER, ae.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(LayerException.CONTROLLER, e.getMessage());
		}
		LOGGER.info("forgetPassword() has been ended.");
		return response;
	}

	/**
	 * @param email
	 * @param token
	 * @param redirectUrl
	 * @return
	 * @throws ApplicationException
	 */
	@GetMapping(value = "/password/reset")
	public ModelAndView resetPassword(@QueryParam("email") String email, @QueryParam("token") String token,
			@QueryParam("redirectUrl") String redirectUrl) throws ApplicationException {

		LOGGER.info("resetPassword() has been started.");

		Optional<CommonResponse> result = Optional.empty();
		try {

			String emailClean = SanitizerHealper.cleanString(email);
			if (TypesProxy.isBlankOrNull(emailClean)) {
				throw new InvalidParameterException("Email Id Is Required");
			}

			String tokenClean = SanitizerHealper.cleanString(token);
			if (TypesProxy.isBlankOrNull(tokenClean)) {
				throw new InvalidParameterException("Token Is Required");
			}

			String redirectUrlClean = SanitizerHealper.cleanString(redirectUrl);
			if (TypesProxy.isBlankOrNull(redirectUrlClean)) {
				throw new InvalidParameterException("Redirect Url Is Required");
			}

			result = userService.validateToken(emailClean, tokenClean, redirectUrlClean);
			if (result.get().getHttpStatusCode() != HttpStatus.OK.value()) {
				redirectUrl = redirectUrlClean.split("reset-password?")[0] + "change-password-failure";
			} else {
				redirectUrl = result.get().getMessage();
			}

		} catch (ApplicationException ae) {
			throw new ApplicationException(LayerException.CONTROLLER, ae.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(LayerException.CONTROLLER, e.getMessage());
		}
		LOGGER.info("resetPassword() has been ended.");
		return new ModelAndView("redirect:" + redirectUrl);
	}

	/**
	 * @param forgotReqDto
	 * @return
	 * @throws ApplicationException
	 */
	@PostMapping(value = "/password/reset")
	public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetReqDto resetReqDto) throws ApplicationException {

		LOGGER.info("resetPassword() has been started.");
		ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		Optional<CommonResponse> result = Optional.empty();
		try {

			String email = SanitizerHealper.cleanString(resetReqDto.getUserEmail());
			String password = SanitizerHealper.cleanString(resetReqDto.getUserPassword());
			String token = SanitizerHealper.cleanString(resetReqDto.getToken());

			ResetReqDto dto = new ResetReqDto(email, password, token);

			result = userService.resetPassword(dto);
			if (result.get().getHttpStatusCode() != HttpStatus.OK.value()) {
				response = new ResponseEntity<>(result.get(), HttpStatus.BAD_REQUEST);
			} else {
				response = new ResponseEntity<>(result.get(), HttpStatus.OK);
			}

		} catch (ApplicationException ae) {
			throw new ApplicationException(LayerException.CONTROLLER, ae.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(LayerException.CONTROLLER, e.getMessage());
		}
		LOGGER.info("resetPassword() has been ended.");
		return response;
	}
	
	/**
	 * @param userReqDto
	 * @return
	 * @throws ApplicationException
	 */
	@GetMapping(value = "/generate/credentials")
	public ResponseEntity<?> generateUserCredentials() throws ApplicationException {

		LOGGER.info("generateUserCredentials() has been started.");
		ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);

		Optional<CommonResponse> result = Optional.empty();
		try {

			result = userService.generateUserCredentials();
			if (result.isPresent()) {
				response = new ResponseEntity<>(result.get(), HttpStatus.OK);
			}

		} catch (ApplicationException ae) {
			throw new ApplicationException(LayerException.CONTROLLER, ae.getMessage());
		} catch (Exception e) {
			throw new ApplicationException(LayerException.CONTROLLER, e.getMessage());
		}
		LOGGER.info("generateUserCredentials() has been ended.");
		return response;
	}

}
