package com.thekreatib.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class KafkaProductsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaProductsMicroserviceApplication.class, args);
	}

}
