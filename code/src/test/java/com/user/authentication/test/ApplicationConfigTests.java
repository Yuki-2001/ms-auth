package com.user.authentication.test;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.user.authentication.ApplicationConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApplicationConfig.class })
class ApplicationConfigTests {

	@Test
	void contextLoads() {
	}

}
