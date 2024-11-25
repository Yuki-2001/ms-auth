package com.est.ms.common.utils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * @author srithar_r
 *
 */
@Configuration
@ConfigurationProperties("config")
@RefreshScope
@Validated
@Data
public class ApplicationResourcesConfigProperties {

	@Valid
	private Environment environment = new Environment();

	@Valid
	private Application application = new Application();

	@Data
	public class Environment {

		@NotEmpty
		private String name;

	}

	@Data
	public class Application {

		@NotEmpty
		private String name;
		
		@NotEmpty
		private String url;
	}

}
