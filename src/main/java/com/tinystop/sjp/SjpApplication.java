package com.tinystop.sjp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication 
@EnableJpaAuditing
public class SjpApplication {
	public static void main(String[] args) {
		SpringApplication.run(SjpApplication.class, args);
	}

}
