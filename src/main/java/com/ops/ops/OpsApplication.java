package com.ops.ops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OpsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpsApplication.class, args);
	}

}
