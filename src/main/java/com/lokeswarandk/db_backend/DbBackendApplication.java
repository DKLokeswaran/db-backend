package com.lokeswarandk.db_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
	// exclude = {DataSourceAutoConfiguration.class}
)
public class DbBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbBackendApplication.class, args);
	}

}
