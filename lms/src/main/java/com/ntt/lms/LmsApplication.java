package com.ntt.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EntityScan(basePackages = "com.ntt.lms.pojo")
@EnableJpaRepositories(basePackages = "com.ntt.lms.repository")
public class LmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(LmsApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("1"));
	}

}
