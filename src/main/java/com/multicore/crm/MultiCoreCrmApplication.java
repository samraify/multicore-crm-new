package com.multicore.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class MultiCoreCrmApplication {

	public static void main(String[] args)
    {
		SpringApplication.run(MultiCoreCrmApplication.class, args);
	}

}
