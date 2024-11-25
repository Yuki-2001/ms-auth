package com.user.authentication.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.est.infra.security.web.SanitizerHealper;
import com.est.infra.utils.proxy.TypesProxy;
import com.user.authentication.common.utils.CommonConstants;
import com.user.authentication.model.User;
import com.user.authentication.repository.IUserRepository;

@RestController
@RequestMapping("oauth")
public class CustomOuthController {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomOuthController.class);

	private final TokenStore tokenStore;

	private final BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
	@Autowired
	private IUserRepository userRepository;

	public CustomOuthController(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@PostMapping(value = "revoke")
	public ResponseEntity<String> revokeAccessToken() {

		try {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				OAuth2AuthenticationDetails currentUser = (OAuth2AuthenticationDetails) authentication.getDetails();
				String token = currentUser.getTokenValue();
				OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
				if (accessToken != null) {
					tokenStore.removeAccessToken(accessToken);
					return ResponseEntity.ok("Access token has been successfully expired.");
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body("Access token not found or already expired.");
				}
			}
		} catch (Exception rae) {
			LOGGER.error("Get Session User Info - " + rae.getMessage());
			return ResponseEntity.ok("Exception.");
		}
		return new ResponseEntity<>("{ \"Token\" : \"No token is provided for the users\" }", HttpStatus.BAD_REQUEST);
	}

	@PutMapping(value = "reset")
	public ResponseEntity<String> resetPassword(@RequestParam String newPassword) {

		try {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userName = authentication.getName();
			Optional<User> userOpt = userRepository.findByUserIdAndStatusNot(userName, CommonConstants.IS_DELETED);
			if (userOpt.isPresent()) {
				String newPasswordClean = SanitizerHealper.cleanString(newPassword);
				
				if(TypesProxy.isBlankOrNull(newPasswordClean)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New Password should not be empty");
				}else if(passwordEncoder.matches(newPasswordClean, userOpt.get().getUserPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New Password should not be same as old Password");
				}
				User user = userOpt.get();
				user.setUserPassword(passwordEncoder.encode(newPassword));

				userRepository.save(user);

				OAuth2AuthenticationDetails currentUser = (OAuth2AuthenticationDetails) authentication.getDetails();
				String token = currentUser.getTokenValue();
				OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
				if (accessToken != null) {
					tokenStore.removeAccessToken(accessToken);
				}
				return ResponseEntity.ok("Password has been successfully changed.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Access token not found or already expired.");

			}
		} catch (Exception rae) {
			LOGGER.error("Get Session User Info - " + rae.getMessage());
			return new ResponseEntity<>("Exception: " + rae.getMessage(), HttpStatus.BAD_REQUEST);
		}
//		return new ResponseEntity<>("{ \"Token\" : \"No token is provided for the users\" }", HttpStatus.BAD_REQUEST);
	}
}
