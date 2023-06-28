package com.econtract.esign;

import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EsignApplication {

	@PostConstruct
	public void init() {
//        dataFeeder.init();
	}

	public static void main(String[] args) {
		SpringApplication.run(EsignApplication.class, args);
	}
}
