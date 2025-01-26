package com.d109.reper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ReperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReperApplication.class, args);
	}

}
