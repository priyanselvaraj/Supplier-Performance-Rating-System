package com.supplier.sprsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entrypoint for Supplier Performance Rating System (SPRS) Backend.
 * Follows Spring Boot 3.x standards with Spring Security, Spring Data JPA,
 * and RESTful API architecture.
 */
@SpringBootApplication
@EnableAsync
public class SprSystemApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(SprSystemApplication.class, args);
    }

    private static void loadDotEnv() {
        Path[] possiblePaths = new Path[]{
                Paths.get(".env"),
                Paths.get("../.env"),
                Paths.get("backend/.env")
        };

        for (Path p : possiblePaths) {
            if (Files.exists(p) && Files.isRegularFile(p)) {
                try (BufferedReader reader = Files.newBufferedReader(p)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }
}
