package com.batch_p2;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class BatchP2Application {

	public static void main(String[] args) {
		SpringApplication.run(BatchP2Application.class, args);
	}

}

