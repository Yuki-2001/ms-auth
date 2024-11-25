package com.est.ms.common.service;

import com.est.ms.email.common.exception.AppSupportEmailException;

/**
 * @author srithar_r
 *
 */
public interface IAppSupportEmailService {
	
	/**
	 * Sent an email notification through daemon thread. The daemon thread is named as the following nomenclature :
	 * <CODE> {APPLICATION_NAME}-supportemail-{YYYYMMDD-HHMMSS}</CODE>
	 *
	 * @param t Exception throw while exception occurs 
	 * @param subject of the email
	 * @param bodyMessage Body of the email content
	 * @throws AppSupportEmailException if any error happens during execution time. 
	 */
	void sentEmailHtmlDeamonEmail(Throwable t, String subject, String bodyMessage) throws AppSupportEmailException;

	/**
	 * Sends an HTML Email Message adding into the body the stack trace of an Exception 
	 * @param exception throwable exception message
	 * @param subject email subject
	 * @param bodyMessage body message content
	 * @throws AppSupportEmailException if any error happen during execution time
	 */
	void sentHtmlMail(Throwable exception, String subject, String bodyMessage) throws AppSupportEmailException;
	
	/**
	 * Sends an HTML Email Message
	 * @param subject email subject
	 * @param bodyMessage body message content
	 * @throws AppSupportEmailException if any error happen during execution time
	 */
	void sentHtmlMail(String subject, String bodyMessage) throws AppSupportEmailException;

}
