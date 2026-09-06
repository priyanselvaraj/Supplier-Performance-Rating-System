package com.supplier.sprsystem.security;

import com.supplier.sprsystem.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final int testExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    @DisplayName("Test generate token and extract username")
    void testGenerateTokenAndExtractUsername() {
        String username = "testuser";
        String token = jwtUtils.generateTokenFromUsername(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Test validate valid JWT token")
    void testValidateValidJwtToken() {
        String token = jwtUtils.generateTokenFromUsername("validuser");

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Test validate invalid / malformed JWT token")
    void testValidateMalformedJwtToken() {
        String invalidToken = "invalid.token.structure";

        boolean isValid = jwtUtils.validateJwtToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test validate expired JWT token")
    void testValidateExpiredJwtToken() {
        JwtUtils expiredJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtExpirationMs", -1000); // Already expired

        String expiredToken = expiredJwtUtils.generateTokenFromUsername("expireduser");

        boolean isValid = jwtUtils.validateJwtToken(expiredToken);
        assertFalse(isValid);
    }
}
