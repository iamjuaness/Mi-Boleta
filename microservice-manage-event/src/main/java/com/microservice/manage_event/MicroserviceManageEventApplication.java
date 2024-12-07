package com.microservice.manage_event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroserviceManageEventApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceManageEventApplication.class, args);
	}

}
