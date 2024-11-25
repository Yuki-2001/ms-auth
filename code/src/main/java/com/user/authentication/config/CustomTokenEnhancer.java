package com.user.authentication.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.user.authentication.common.utils.CommonConstants;
import com.user.authentication.repository.IUserRepository;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Autowired
	private IUserRepository userRepository;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		final Map<String, Object> additionalInfo = new HashMap<>();
		Optional<com.user.authentication.model.User> userOpt = userRepository
				.findByUserIdAndStatusNot(user.getUsername(), CommonConstants.IS_DELETED);

		if (userOpt.isPresent()) {

			additionalInfo.put("user_id", userOpt.get().getUserId());
			additionalInfo.put("user_email", userOpt.get().getUserEmail());
//			additionalInfo.put("user_domain", userOpt.get().getUserDomain());
			additionalInfo.put("user_name", userOpt.get().getUserName());
			additionalInfo.put("user_department", userOpt.get().getUserDepartment());
			additionalInfo.put("user_role", userOpt.get().getUserRole());
		}

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

		return accessToken;
	}

}
