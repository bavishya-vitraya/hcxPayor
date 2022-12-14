package com.hcx.hcxPayor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HcxPayorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HcxPayorApplication.class, args);
	}
	@GetMapping("/")
	public ResponseEntity<String> checkHcxPayor() {
		return  ResponseEntity.ok("Test Payor Service");
	}
}
