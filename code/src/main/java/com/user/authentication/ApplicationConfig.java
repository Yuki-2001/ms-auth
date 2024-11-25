package com.user.authentication;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.est.ms.common.service.IAppSupportEmailService;
import com.est.ms.common.utils.ApplicationResourcesConfigProperties;
import com.est.ms.email.common.exception.AppSupportEmailException;
import com.est.ms.email.common.utils.ApplicationResourcesEmailConfigProperties;

/**
 * @author srithar_r
 *
 */
@SpringBootApplication

@ComponentScan({"com"})

@EntityScan({"com.user.authentication"})

@EnableJpaRepositories

@EnableConfigurationProperties({
	ApplicationResourcesConfigProperties.class,
	ApplicationResourcesEmailConfigProperties.class
})
public class ApplicationConfig {
	
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);
	
	@Autowired
	private IAppSupportEmailService appSupportEmailService;
	
	private ApplicationResourcesConfigProperties applicationResourcesConfigProperties;

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfig.class, args);
	}
	
	@PreDestroy
	public void preDestroy() {
		
		String message = applicationResourcesConfigProperties.getApplication().getName() + " microservice has been stopped";
		
		LOGGER.info(message);
		
		try {
			
			appSupportEmailService.sentHtmlMail(message, message);
			
		}catch(AppSupportEmailException | Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
