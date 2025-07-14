package com.md.service_request_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ServiceRequestSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRequestSystemApplication.class, args);
	}

}
