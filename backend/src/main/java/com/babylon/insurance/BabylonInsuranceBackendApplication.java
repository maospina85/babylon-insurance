package com.babylon.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class BabylonInsuranceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BabylonInsuranceBackendApplication.class, args);
	}

}
