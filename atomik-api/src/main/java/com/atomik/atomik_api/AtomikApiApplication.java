package com.atomik.atomik_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtomikApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtomikApiApplication.class, args);
	}

}
