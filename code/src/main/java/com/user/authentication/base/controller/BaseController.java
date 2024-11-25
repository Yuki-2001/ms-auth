package com.user.authentication.base.controller;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;

import com.est.infra.utils.exception.ApplicationException;
import com.est.infra.utils.exception.LayerException;

/**
 * @author srithar_r
 *
 */
public abstract class BaseController {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	protected static final String JSON_STATUS_SUCCESS = "SUCCESS";
	
	protected static final String JSON_STATUS_FAIL = "FAIL";
	
	protected static final String DATABASE_STATUS_UP = "UP";
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	private HealthIndicator dbHealthIndicator(){
		DataSourceHealthIndicator indicator = new DataSourceHealthIndicator(dataSource);
		indicator.setQuery("SELECT 1");
		return indicator;
	}
	
	public String ping()throws Exception {
		
		StringBuilder htmlMessageBuilder = new StringBuilder();
		String message = "ping has started...";
		LOGGER.info(message);
		htmlMessageBuilder.append("<p>").append(message).append("</p>");
		int errorsCounter = 0;
		
		try{
			
			message = "Validating database connectivity ...";
			LOGGER.info(message);
			htmlMessageBuilder.append("<p>").append(message).append("</p>");
			
			HealthIndicator hi = dbHealthIndicator();
			Health ht = hi.health();
			
			Status status = ht.getStatus();
			
			if(DATABASE_STATUS_UP.equalsIgnoreCase(status.getCode())){
				throw new ApplicationException(LayerException.CONTROLLER,"Databse status code: "+status.getCode()+" with description "+ status.getDescription());
			}
			
			LOGGER.info("DataBase status code: "+status.getCode()+" with description " +status.getDescription());
			
			message = "DataBase connectivity: ok";
			
			LOGGER.info(message);
			
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			message = "DataBase Connetivity: FAILED with error: "+e.getMessage();
			errorsCounter++;
		}
		
		htmlMessageBuilder.append("<p>").append(message).append("</p>");
		
		try{
			
			message = "Validating SMTP connectivity...";
			LOGGER.info(message);
			htmlMessageBuilder.append("<p>").append(message).append("</p>");
			
			message = "SMTP connectivity: ok";
			
			LOGGER.info(message);
			
		}catch(Exception e){//AppSupportEmailException
			LOGGER.error(e.getMessage());
			message = "Email sening: FAILED with error: "+e.getMessage();
			errorsCounter++;
		}
		 
		String status = errorsCounter!=0?JSON_STATUS_FAIL:JSON_STATUS_SUCCESS;
		String messageJson = errorsCounter!=0?message:JSON_STATUS_SUCCESS;
		return "{\"status\":\""+status+"\",\"message\":\""+messageJson+"\"}";
	}
}

