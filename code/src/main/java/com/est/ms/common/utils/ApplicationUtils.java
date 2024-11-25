package com.est.ms.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author srithar-r
 * 
 *         Utilities class related with the Application's configuration
 *
 */
public final class ApplicationUtils {

	/**
	 * Get Current Date
	 * 
	 * @return {@link LocalDate}
	 */
	public static final LocalDate getSystemDate() {
		return LocalDate.now();
	}

	/**
	 * Get Current Date Time
	 * 
	 * @return {@link LocalDateTime}
	 */
	public static final LocalDateTime getSystemDateTime() {
//		return LocalDateTime.now();
//		return ZonedDateTime.now(ZoneId.of("Japan")).toLocalDateTime();
		return LocalDateTime.now(ZoneOffset.UTC);
	}

	/**
	 * get the Current Year
	 * 
	 * @return int
	 */
	public static final int getSystemYear() {
		return getSystemDate().getYear();
	}

}
