package com.supplier.sprsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Component
public class ProductionEnvironmentValidator implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ProductionEnvironmentValidator.class);
    private static final String DEFAULT_DEV_JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private final Environment environment;

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    public ProductionEnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.asList(activeProfiles).contains("prod");

        logger.info("================================================================================");
        logger.info(" SPRS Application Startup Verified — Active Profiles: {}", Arrays.toString(activeProfiles));

        if (isProduction) {
            logger.info(" Production Profile Active: Performing Strict Security & Environment Audits...");
            validateProductionEnvironment();
            logger.info(" Production Environment Validation: PASSED (All security checks satisfied)");
        } else {
            logger.info(" Development / Test Profile Active: Development defaults permitted.");
        }
        logger.info("================================================================================");
    }

    public void validateProductionEnvironment() {
        // 1. JWT Secret Verification
        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException("CRITICAL PRODUCTION SECURITY ERROR: 'JWT_SECRET' environment variable is missing!");
        }

        if (jwtSecret.length() < 32) {
            throw new IllegalStateException("CRITICAL PRODUCTION SECURITY ERROR: 'JWT_SECRET' must be at least 256 bits (32+ characters) long!");
        }

        if (DEFAULT_DEV_JWT_SECRET.equalsIgnoreCase(jwtSecret.trim())) {
            throw new IllegalStateException("CRITICAL PRODUCTION SECURITY ERROR: Default development JWT secret detected in production profile! You must override 'JWT_SECRET' with a secure private key.");
        }

        // 2. Datasource URL Verification
        if (!StringUtils.hasText(datasourceUrl)) {
            throw new IllegalStateException("CRITICAL PRODUCTION CONFIGURATION ERROR: Datasource URL is not configured!");
        }

        // 3. CORS Configuration Verification
        if (StringUtils.hasText(allowedOrigins) && allowedOrigins.contains("*")) {
            logger.warn("SECURITY WARNING: Wildcard '*' detected in production CORS allowed-origins. Restricting to specific domains is recommended.");
        }
    }
}
