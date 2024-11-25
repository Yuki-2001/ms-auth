package com.user.authentication.common.utils;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory {

private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateFactory.class);
	
	private static HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
	private static RestTemplate restTemplate;
	
	// Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT_MILLS = 5000;
    
	// The timeout when requesting a connection from the connection manager
	private static final int REQUEST_CONNECTION_TIMEOUT_MILLS = 5000;
	
	@PostConstruct
	private void init() {

		LOGGER.info("Initializing {} static instance",this.getClass().getSimpleName());
		LOGGER.info("Connection Timeout (mills): {}",CONNECT_TIMEOUT_MILLS);
		LOGGER.info("Connection Request Timeout (mills): {}",REQUEST_CONNECTION_TIMEOUT_MILLS);
		
		//unsafe to add message converters on a reusable rest template
		httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpComponentsClientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT_MILLS);
		httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(REQUEST_CONNECTION_TIMEOUT_MILLS);
		
		//is thread-safe once constructed
		restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);	
	}
	
	public static final RestTemplate getRestTemplate() {
		return restTemplate;
	}
}
