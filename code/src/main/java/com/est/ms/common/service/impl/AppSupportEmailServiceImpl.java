package com.est.ms.common.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.est.infra.utils.helper.TimeHelper;
import com.est.ms.common.service.IAppSupportEmailService;
import com.est.ms.common.utils.ApplicationResourcesConfigProperties;
import com.est.ms.common.utils.ApplicationResourcesFileHelper;
import com.est.ms.email.common.domain.ContactInfo;
import com.est.ms.email.common.domain.EmailSupportRequest;
import com.est.ms.email.common.domain.EmailTemplateRequest;
import com.est.ms.email.common.domain.MetaData;
import com.est.ms.email.common.exception.AppSupportEmailException;
import com.est.ms.email.common.utils.ApplicationResourcesEmailConfigProperties;
import com.est.ms.email.common.utils.ApplicationResourcesEmailFileConstants;
import com.est.ms.email.service.IEmailSenderService;

/**
 * @author srithar_r
 *
 */
@Service
public class AppSupportEmailServiceImpl implements IAppSupportEmailService {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AppSupportEmailServiceImpl.class);

	/**
	 * emailSenderServiceImpl IEmailNotifycationService
	 */
	@Autowired
	private IEmailSenderService emailSenderServiceImpl;

	/**
	 * applicationResourcesConfigProperties ApplicationResourcesConfigProperties
	 */
	@Autowired
	private ApplicationResourcesConfigProperties applicationResourcesConfigProperties;

	@Autowired
	private ApplicationResourcesEmailConfigProperties applicationResourcesEmailConfigProperties;

	/*
	 * (non javaDoc)
	 * 
	 * @see
	 * com.est.ms.common.service.IAppSupportEmailService#sentHtmlMail(java.lang.
	 * Throwable, java.lang.String, java.lang.String)
	 */
	@Override
	public void sentHtmlMail(Throwable exception, String subject, String bodyMessage) throws AppSupportEmailException {

		if (exception != null) {
			LOGGER.debug("[sentHtmlMail] has started for input parameters : subject {}, bodyMessage {}, exception {}",
					subject, bodyMessage, exception.getMessage());
		} else {
			LOGGER.debug("[sentHtmlMail] has started for input parameters : subject {}, bodyMessage {}", subject,
					bodyMessage);
		}

		try {

			emailSenderServiceImpl.sendTechSupportEmail(getRequestBody(getHtmlBody(exception, bodyMessage)));

		} catch (Exception e) {
			LOGGER.error("[sentHtmlMail] Error sending technical support email. {}, {} ",
					e.getClass().getCanonicalName(), e.getMessage(), e);

			throw new AppSupportEmailException(e);
		}

	}

	/*
	 * (non javaDoc)
	 * 
	 * @see
	 * com.est.ms.common.service.IAppSupportEmailService#sentHtmlMail(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void sentHtmlMail(String subject, String bodyMessage) throws AppSupportEmailException {
		sentHtmlMail(null, subject, bodyMessage);
	}

	/*
	 * (non javaDoc)
	 * 
	 * @see
	 * com.est.ms.common.service.IAppSupportEmailService#sentEmailHtmlDeamonEmail(
	 * java.lang.Throwable, java.lang.String, java.lang.String)
	 */
	@Override
	public void sentEmailHtmlDeamonEmail(Throwable t, String subject, String bodyMessage)
			throws AppSupportEmailException {

		if (t != null) {
			LOGGER.debug(
					"[sentEmailHtmlDeamonEmail] has stated for input parameters: subject {} bodyMessage {} exception {}",
					subject, bodyMessage, t.getMessage());
		} else {
			LOGGER.debug("[sentEmailHtmlDeamonEmail] has stated for input parameters: subject {} bodyMessage {} ",
					subject, bodyMessage);
		}

		try {

			String emailThreadName = new StringBuilder(applicationResourcesConfigProperties.getApplication().getName())
					.append("-supportemail-").append(TimeHelper.formatLocalDateTimeToString(LocalDateTime.now(),
							TimeHelper.DateTimeHelperFormat.YYYYDDMM_HHMMSS, Locale.US).get())
					.toString();

			ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = Executors.defaultThreadFactory().newThread(runnable);
					thread.setDaemon(true);
					thread.setName(emailThreadName);
					return thread;
				}
			});

			executorService.submit(() -> {

				LOGGER.info("[sentEmailHtmlDeamonEmail] Sending process execution email notification to "
						+ "Technical Support Team. Thread name: {}", emailThreadName);

				try {
					emailSenderServiceImpl.sendTechSupportEmail(getRequestBody(getHtmlBody(t, bodyMessage)));
				} catch (Exception e) {
					LOGGER.error(
							"[sentEmailHtmlDeamonEmail] Error sending email through daemon thread " + "{} ERROR: {}",
							emailThreadName, e.getMessage(), e);
				}
			});

			// no longer accept any new task
			executorService.shutdown();
		} catch (Exception ex) {
			LOGGER.error("[sentEmailHtmlDeamonEmail] Error sending of technical support email. {}: {}",
					ex.getClass().getCanonicalName(), ex.getMessage());
			throw new AppSupportEmailException(ex);
		}
	}

	/**
	 * @param t           Throwable
	 * @param bodyMessage String
	 * @return emailMessage String
	 */
	private String getHtmlBody(Throwable t, String bodyMessage) {

		StringBuilder emailMessage = new StringBuilder()
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >")
				.append("<span><b>").append(bodyMessage).append("</b></span><br>").append("</p>");

		if (t != null) {
			emailMessage
					.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
					.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >")
					.append("<span style=\"color:red;\">Stack Trace:</span> ").append(ExceptionUtils.getStackTrace(t))
					.append("</br>").append("</p>")
					.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
					.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >")
					.append("<span style=\"color:red;\">Root Cause:</span> ")
					.append(ExceptionUtils.getRootCauseMessage(t)).append("</p>");
		}

		return emailMessage.toString();

	}

	/**
	 * @param bodyMessage String
	 * @return EmailSupportRequest Object
	 */
	private EmailSupportRequest getRequestBody(String bodyMessage) {

		MetaData metadata = new MetaData(applicationResourcesConfigProperties.getApplication().getName(),
				applicationResourcesConfigProperties.getEnvironment().getName(),
				ApplicationResourcesFileHelper.getHostName().orElse("Unknown Host Name"),
				ApplicationResourcesFileHelper.getHostIp().orElse("Unknown Host IP"));

		String to = applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getToEmail()
				.split(",")[0];
		List<String> cc = null;
		if (applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getCcEmail()
				.split(",").length >= 1) {
			cc = Arrays.asList(applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getCcEmail()
					.split(","));
		}

		return new EmailSupportRequest(metadata,
				applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getEmailSubject(),
				bodyMessage, applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromEmail(),
				applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromName(), to, cc, null,
				null, null);

	}

	/**
	 * @param bodyMessage
	 * @return
	 */
	public String getHtmlBody(String bodyMessage) {

		StringBuilder emailMessage = new StringBuilder()
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >")
				.append("<span>").append(bodyMessage).append("</span><br>").append("</p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p style=\"font-size:12px;font-family:Arial, sans-serif;Margin-bottom:10px; color:#454545\" >")
				.append("<span><b>").append("Regards,<br>Chrysalis Tech Support Team.").append("</b></span><br></p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p align=\"center\" style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px; background-color: grey;\" >")
				.append("<span style=\"font-size:12px;color:red;\"><b>")
				.append("This email has sent from an automated system - please do not reply.")
				.append("</b></span></p>");

		return emailMessage.toString();

	}

	/**
	 * @param bodyMessage String
	 * @return EmailSupportRequest Object
	 */
	public EmailTemplateRequest getRequestBody(String toEmail, String subject, String bodyMessage) {

		bodyMessage = getHtmlBody(bodyMessage);

		MetaData metadata = new MetaData(applicationResourcesConfigProperties.getApplication().getName(),
				applicationResourcesConfigProperties.getEnvironment().getName(),
				ApplicationResourcesFileHelper.getHostName().orElse("Unknown Host Name"),
				ApplicationResourcesFileHelper.getHostIp().orElse("Unknown Host IP"));

		String[] ccListMailVar = null;

		if (ApplicationResourcesEmailFileConstants.ENVIRONMENT_PRODUCTION
				.equalsIgnoreCase(applicationResourcesConfigProperties.getEnvironment().getName())) {
			ccListMailVar = null;
		} else {

			List<String> cc = null;
			if (applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getCcEmail()
					.split(",").length >= 1) {
				cc = Arrays.asList(applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport()
						.getCcEmail().split(","));
			}
			ccListMailVar = cc.stream().toArray(size -> new String[size]);
		}

		ContactInfo contactInfo = new ContactInfo(
				applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromEmail(),
				applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromName(), toEmail,
				ccListMailVar, null);

		return new EmailTemplateRequest(metadata, contactInfo, subject, bodyMessage, null, null);
	}

	/**
	 * @param bodyMessage
	 * @return
	 */
	public String getHtmlBodyWithoutFooter(String bodyMessage) {

		StringBuilder emailMessage = new StringBuilder()
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >")
				.append("<span>").append(bodyMessage).append("</span><br>").append("</p>")
				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
				.append("<p style=\"font-size:12px;font-family:Arial, sans-serif;Margin-bottom:10px; color:#454545\" >")
				.append("<span><b>").append("Regards,<br>Chrysalis Tech Support Team.").append("</b></span><br></p>");
//				.append("<p style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px;\" >&nbsp;</p>")
//				.append("<p align=\"center\" style=\"font-size:14px;font-family:Arial, sans-serif;Margin-bottom:10px; background-color: grey;\" >")
//				.append("<span style=\"font-size:12px;color:red;\"><b>")
//				.append("This email has sent from an automated system - please do not reply.")
//				.append("</b></span></p>");

		return emailMessage.toString();

	}

	/**
	 * @param bodyMessage String
	 * @return EmailSupportRequest Object
	 */
	public EmailTemplateRequest getRequestBodyWithoutFooter(String toEmail, String subject, String bodyMessage) {

		bodyMessage = getHtmlBodyWithoutFooter(bodyMessage);

		MetaData metadata = new MetaData(applicationResourcesConfigProperties.getApplication().getName(),
				applicationResourcesConfigProperties.getEnvironment().getName(),
				ApplicationResourcesFileHelper.getHostName().orElse("Unknown Host Name"),
				ApplicationResourcesFileHelper.getHostIp().orElse("Unknown Host IP"));

		String[] ccListMailVar = null;

		if (ApplicationResourcesEmailFileConstants.ENVIRONMENT_PRODUCTION
				.equalsIgnoreCase(applicationResourcesConfigProperties.getEnvironment().getName())) {
			ccListMailVar = null;
		} else {

			List<String> cc = null;
			if (applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getCcEmail()
					.split(",").length >= 1) {
				cc = Arrays.asList(applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport()
						.getCcEmail().split(","));
			}
			ccListMailVar = cc.stream().toArray(size -> new String[size]);
		}

		ContactInfo contactInfo = new ContactInfo(applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromEmail(),
				applicationResourcesEmailConfigProperties.getEnvironment().getTechsupport().getFromName(), toEmail,
				ccListMailVar, null);

		return new EmailTemplateRequest(metadata, contactInfo, subject, bodyMessage, null, null);
	}

}
