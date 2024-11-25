package com.user.authentication.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.authentication.base.controller.BaseController;

/**
 * @author srithar_r
 *
 */
@RestController
public class CommonController extends BaseController {
	
	@GetMapping("/ping")
	public String ping() throws Exception {
		return super.ping();
	}
	
}