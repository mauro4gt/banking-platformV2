package com.example.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.customers.infra.repository")
@EntityScan(basePackages = "com.example.customers.domain")
public class CustomersServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersServiceApplication.class, args);
    }
}
