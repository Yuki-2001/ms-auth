package com.user.authentication.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.est.ms.common.utils.ApplicationUtils;
import com.user.authentication.common.utils.CommonConstants;
import com.user.authentication.model.User;
import com.user.authentication.repository.IUserRepository;

@Service(value = "loginService")
public class LoginServiceImpl implements UserDetailsService {

	@Autowired
	private IUserRepository userRepository;

	/*
	 * (non javaDoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		String userPassword = "";
		Optional<User> userOpt = userRepository.findByUserIdAndStatusNot(userName, CommonConstants.IS_DELETED);

		if (userOpt.isPresent()) {
			if (userOpt.get().getStatus().equalsIgnoreCase(CommonConstants.IS_ACTIVE)) {
				User user = userOpt.get();
				
				user.setLastLoginDate(ApplicationUtils.getSystemDateTime());
				userRepository.save(user);
				
				
				userName = user.getUserId();
				userPassword = user.getUserPassword();

			} else if (userOpt.get().getStatus().equalsIgnoreCase(CommonConstants.IS_INACTIVE)) {
				throw new UsernameNotFoundException("User is Deactivated");
			}
		} else {
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		return new org.springframework.security.core.userdetails.User(userName, userPassword, getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		List<SimpleGrantedAuthority> list = new ArrayList<SimpleGrantedAuthority>();
		// Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		list.add(new SimpleGrantedAuthority("ROLE_USER"));
		return list;
	}

}
