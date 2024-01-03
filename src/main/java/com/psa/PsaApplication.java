package com.psa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PsaApplication.class, args);
	}

}
