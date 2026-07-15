package com.mas.co;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;

@SpringBootApplication(exclude = {MultipartAutoConfiguration.class})
public class AcueductoBackendApplication {

		public static void main(String[] args) {
				SpringApplication.run(AcueductoBackendApplication.class, args);
		}

}
