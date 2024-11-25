package com.est.ms.common.handler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.est.infra.utils.exception.ApplicationException;
import com.est.infra.utils.helper.TimeHelper;
import com.est.ms.common.service.IAppSupportEmailService;
import com.est.ms.common.utils.ApplicationResourcesConfigProperties;
import com.est.ms.email.common.domain.ExceptionResponse;
import com.est.ms.email.common.exception.AppSupportEmailException;

/**
 * @author srithar_r
 *
 */
@ControllerAdvice
@RestController
public class ApplicationExceptionResponseEntityHandler extends ResponseEntityExceptionHandler {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionResponseEntityHandler.class);
	
	/**
	 * appSupportEmailService IAppSupportEmailService
	 */
	@Autowired
	private final IAppSupportEmailService appSupportEmailService;
	
	@Autowired
	private ApplicationResourcesConfigProperties applicationResourcesConfigProperties;
	
	/**
	 * @param appSupportEmailService IAppSupportEmailService
	 */
	public ApplicationExceptionResponseEntityHandler(IAppSupportEmailService appSupportEmailService) {		
		this.appSupportEmailService = appSupportEmailService;
	}
	
	/**
	 * @param ae ApplicationException
	 * @param request WebRequest
	 * @param locale Locale
	 * @return exceptionResponse ExceptionResponse
	 */
	@ExceptionHandler(ApplicationException.class)
	public final ResponseEntity<ExceptionResponse> handleException(ApplicationException ae, WebRequest request, Locale locale){
		
		LOGGER.error("An Exception has been detected processing a request. Sending email notification to technical support team");
		
		LocalDateTime now = LocalDateTime.now();
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				"ERROR",
				TimeHelper.formatLocalDateTimeToString(now, TimeHelper.DateTimeHelperFormat.YYYYDDMM_HHMMSS, locale).get(),
				Timestamp.valueOf(now).getTime(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ae.getMessage(),
				request.getDescription(false));
		try{
			
			appSupportEmailService.sentEmailHtmlDeamonEmail(ae, applicationResourcesConfigProperties.getApplication().getName()+" ERROR", 
					"Erorr performing request: "+request.getDescription(false));
			
		} catch(AppSupportEmailException e){
			LOGGER.error("[handleException] Error sending email notification to technical support team of the application. "
					+ "{}:{}", e.getClass().getCanonicalName(), e.getMessage(), e);			
		} 
		
		return new ResponseEntity<>(exceptionResponse , HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
