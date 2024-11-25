package com.est.ms.common.utils;

import java.net.InetAddress;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author srithar_r
 *
 */
@Component
public class ApplicationResourcesFileHelper {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResourcesFileHelper.class);

	/**
	 * @return the host name
	 */
	public static final Optional<String> getHostName() {

		Optional<String> response = Optional.empty();
		try {
			response = Optional.of(InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			LOGGER.warn("[getHostName] Host name cannot be determined.");
		}

		return response;
	}

	/**
	 * @return the host ip
	 */
	public static final Optional<String> getHostIp() {

		Optional<String> response = Optional.empty();
		try {
			response = Optional.of(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			LOGGER.warn("[getHostName] Host IP cannot be determined.");
		}

		return response;
	}

	/**
	 * @return tech support email template name
	 */
	public static final String getTechSupportEmailTemplate() {
		return ApplicationResourcesFileConstants.TECH_SUPPORT_EMAIL_TEMPLATE;
	}

}
