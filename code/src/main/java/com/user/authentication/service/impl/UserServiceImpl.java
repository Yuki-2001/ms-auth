package com.user.authentication.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.est.infra.utils.exception.ApplicationException;
import com.est.infra.utils.exception.LayerException;
import com.est.infra.utils.proxy.TypesProxy;
import com.est.ms.common.service.impl.AppSupportEmailServiceImpl;
import com.est.ms.common.utils.ApplicationResourcesConfigProperties;
import com.est.ms.common.utils.ApplicationUtils;
import com.est.ms.email.common.domain.EmailTemplateRequest;
import com.est.ms.email.common.domain.EmailTemplateResponse;
import com.est.ms.email.service.IEmailSenderService;
import com.user.authentication.common.utils.CommonConstants;
import com.user.authentication.common.utils.CommonResponse;
import com.user.authentication.dto.ForgotReqDto;
import com.user.authentication.dto.ResetReqDto;
import com.user.authentication.dto.UserReqDto;
import com.user.authentication.model.User;
import com.user.authentication.repository.IUserRepository;
import com.user.authentication.service.IUserService;

/**
 * @author srithar_r
 *
 */
@Service
public class UserServiceImpl implements IUserService {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private static final int RESET_PASSWORD_TOKEN_EXPIRY_MINUTES = 60;

	private static final int RESET_PASSWORD_TOKEN_EMAIL_EXPIRY_MINUTES = 7 * 24 * 60;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IEmailSenderService emailSenderServiceImpl;

	@Autowired
	private AppSupportEmailServiceImpl appSupportEmailServiceImpl;

	@Autowired
	private ApplicationResourcesConfigProperties applicationResourcesConfigProperties;
	

	/**
	 * (non-javaDoc)
	 *
	 * @see com.user.authentication.service.IUserService#saveUserDetails(com.user.authentication.dto.UserReqDto, java.util.Map)
	 */
	@Override
	public Optional<Boolean> saveUserDetails(UserReqDto userReqDtoClean, Map<String, String> errors)
			throws ApplicationException {
		LOGGER.info("saveProjectDetails() Has Started At Service");
		Optional<Boolean> response = Optional.of(Boolean.TRUE);

		try {

			Optional<User> opt1 = userRepository.findByUserEmailAndStatus(userReqDtoClean.getUserEmail(), CommonConstants.IS_ACTIVE);
			if (opt1.isPresent()) {
				errors.put("userEmail", "User Email already exist");
				response = Optional.of(Boolean.FALSE);
			}
			
			Optional<User> opt2 = userRepository.findByUserIdAndStatus(userReqDtoClean.getUserId(), CommonConstants.IS_ACTIVE);
			if (opt2.isPresent()) {
				errors.put("userId", "User Id already exist");
				response = Optional.of(Boolean.FALSE);
			}

			if (response.get()) {
				User user = new User();
				user.setUserId(userReqDtoClean.getUserId());
				user.setUserName(userReqDtoClean.getUserName());
				user.setUserDepartment(userReqDtoClean.getUserDepartment());
				user.setUserEmail(userReqDtoClean.getUserEmail());
				user.setUserPassword(passwordEncoder().encode("Password"));
				user.setUserRole("USER");
				user.setStatus(CommonConstants.IS_ACTIVE);
				user.setCreatedOn(ApplicationUtils.getSystemDateTime());
				user.setCreatedBy(userReqDtoClean.getUserId());
				user.setIsEmailSent(false);
				userRepository.save(user);
			}

		} catch (

		Exception e) {
			throw new ApplicationException(LayerException.SERVICE, e.getMessage());
		}
		LOGGER.info("saveProjectDetails() Has Ended At Service");
		return response;
	}

	/**
	 * @see com.user.authentication.service.IUserService#forgotPassword(com.user.authentication.dto.ForgotReqDto)
	 */
	@Override
	public Optional<String> forgotPassword(@Valid ForgotReqDto forgotReqDto) throws ApplicationException {

		LOGGER.info("forgotPassword method init.");
		Optional<String> response = Optional.empty();
		try {
			User user = new User();
			Optional<User> userOpt = userRepository.findByUserEmailAndStatusNot(forgotReqDto.getUserEmail(),
					CommonConstants.IS_DELETED);
			if (userOpt.isPresent()) {
				user = userOpt.get();

				String resetPasswordToken = generateToken();

				user.setResetPasswordToken(resetPasswordToken);
				user.setResetPasswordTokenCreation(ApplicationUtils.getSystemDateTime());
				user.setUpdatedBy(user.getUserId().toString());
				user.setUpdatedOn(ApplicationUtils.getSystemDateTime());

				userRepository.save(user);

				String encodedEmailString = Base64.getEncoder().encodeToString(user.getUserEmail().getBytes());
				String redirectUrl = forgotReqDto.getRedirectUrl();

				String appUrl = applicationResourcesConfigProperties.getApplication().getUrl();

				appUrl += "/user/password/reset?email=" + encodedEmailString;
				appUrl += "&token=" + resetPasswordToken;
				appUrl += "&redirectUrl=" + redirectUrl;

				String bodyMessage = new StringBuilder().append("<p>Hello <b>" + user.getUserName() + "</b>,</p>")
						.append("<p>You have requested to reset your password.</p>")
						.append("<p>Click the link below to change your password:</p>")
						.append("<p><b><u><a href='" + appUrl + "'>Click Here</a></u></b></p>").append("<br>")
						.append("<p>Ignore this email if you do remember your password, ")
						.append("or you have not made the request.</p>").toString();

				EmailTemplateRequest emailTemplateRequest = appSupportEmailServiceImpl
						.getRequestBody(user.getUserEmail(), "Reset Password", bodyMessage);

				EmailTemplateResponse result = emailSenderServiceImpl.sendEmail(emailTemplateRequest);

				if (!TypesProxy.isNull(result)) {
					response = Optional.of("Success");
				}
			}
		} catch (Exception e) {
			throw new ApplicationException(LayerException.SERVICE, e.getMessage());
		}
		LOGGER.info("forgotPassword method end.");
		return response;
	}

	/**
	 * @see com.user.authentication.service.IUserService#validateToken(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<CommonResponse> validateToken(String email, String token, String redirectUrl)
			throws ApplicationException {

		LOGGER.info("validateToken method init.");
		Optional<CommonResponse> response = Optional.empty();
		try {
			User user = new User();
			byte[] decodedBytes = Base64.getDecoder().decode(email);
			String decodedEmailString = new String(decodedBytes);

			Optional<User> userOpt = userRepository.findByUserEmailAndResetPasswordTokenAndStatusNot(decodedEmailString,
					token, CommonConstants.IS_DELETED);
			if (userOpt.isPresent()) {
				LocalDateTime tokenCreationDate = userOpt.get().getResetPasswordTokenCreation();

				if (isTokenExpired(tokenCreationDate, RESET_PASSWORD_TOKEN_EMAIL_EXPIRY_MINUTES)) {
					response = Optional.of(new CommonResponse("Token Has Expired", HttpStatus.UNAUTHORIZED.value(),
							HttpStatus.UNAUTHORIZED.getReasonPhrase(), null));
				} else {

					user = userOpt.get();

					String resetPasswordToken = generateToken();

					user.setResetPasswordToken(resetPasswordToken);
					user.setResetPasswordTokenCreation(ApplicationUtils.getSystemDateTime());
					user.setUpdatedBy(user.getUserId().toString());
					user.setUpdatedOn(ApplicationUtils.getSystemDateTime());

					userRepository.save(user);

					redirectUrl += "?email=" + email;
					redirectUrl += "&token=" + resetPasswordToken;

					response = Optional.of(new CommonResponse(redirectUrl, HttpStatus.OK.value(),
							HttpStatus.OK.getReasonPhrase(), null));
				}
			} else {
				response = Optional.of(new CommonResponse("Invalid Token", HttpStatus.UNAUTHORIZED.value(),
						HttpStatus.UNAUTHORIZED.getReasonPhrase(), null));
			}

		} catch (Exception e) {
			throw new ApplicationException(LayerException.SERVICE, e.getMessage());
		}
		LOGGER.info("validateToken method end.");
		return response;
	}

	/**
	 * @see com.user.authentication.service.IUserService#resetPassword(com.user.authentication.dto.ResetReqDto)
	 */
	@Override
	public Optional<CommonResponse> resetPassword(@Valid ResetReqDto resetReqDto) throws ApplicationException {

		LOGGER.info("resetPassword method init.");
		Optional<CommonResponse> response = Optional.empty();
		try {
			User user = new User();
			Optional<User> userOpt = userRepository.findByUserEmailAndResetPasswordTokenAndStatusNot(
					resetReqDto.getUserEmail(), resetReqDto.getToken(), CommonConstants.IS_DELETED);
			if (userOpt.isPresent()) {
				LocalDateTime tokenCreationDate = userOpt.get().getResetPasswordTokenCreation();

				if (isTokenExpired(tokenCreationDate, RESET_PASSWORD_TOKEN_EXPIRY_MINUTES)) {
					response = Optional.of(new CommonResponse("Token Has Expired", HttpStatus.UNAUTHORIZED.value(),
							HttpStatus.UNAUTHORIZED.getReasonPhrase(), null));
				} else {
					user = userOpt.get();

					user.setResetPasswordToken(null);
					user.setResetPasswordTokenCreation(null);
					user.setUserPassword(passwordEncoder().encode(resetReqDto.getUserPassword()));
					user.setLastPasswordUpdatedDate(ApplicationUtils.getSystemDateTime());
					user.setUpdatedBy(user.getUserId().toString());
					user.setUpdatedOn(ApplicationUtils.getSystemDateTime());

					userRepository.save(user);

					response = Optional.of(new CommonResponse("Password Resetted Successfully", HttpStatus.OK.value(),
							HttpStatus.OK.getReasonPhrase(), null));
				}
			} else {
				response = Optional.of(new CommonResponse("Invalid Token", HttpStatus.UNAUTHORIZED.value(),
						HttpStatus.UNAUTHORIZED.getReasonPhrase(), null));
			}
		} catch (Exception e) {
			throw new ApplicationException(LayerException.SERVICE, e.getMessage());
		}
		LOGGER.info("resetPassword method end.");
		return response;
	}

//	s

	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Generate unique token. You may add multiple parameters to create a strong
	 * token.
	 * 
	 * @return unique token
	 */
	private String generateToken() {	
		StringBuilder token = new StringBuilder();

		return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
	}

	/**
	 * Check whether the created token expired or not.
	 * 
	 * @param tokenCreationDate
	 * @return true or false
	 */
	private boolean isTokenExpired(final LocalDateTime tokenCreationDate, final int tokenExpiryTime) {

		LocalDateTime now = ApplicationUtils.getSystemDateTime();
		Duration diff = Duration.between(tokenCreationDate, now);

		return diff.toMinutes() >= tokenExpiryTime;
	}

	public static String alphaNumericString(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcedfghijklmnopqrstuvwxyz";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

}
