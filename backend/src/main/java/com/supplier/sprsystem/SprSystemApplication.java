package com.supplier.sprsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entrypoint for Supplier Performance Rating System (SPRS) Backend.
 * Follows Spring Boot 3.x standards with Spring Security, Spring Data JPA,
 * and RESTful API architecture.
 */
@SpringBootApplication
@EnableAsync
public class SprSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprSystemApplication.class, args);
    }
}
