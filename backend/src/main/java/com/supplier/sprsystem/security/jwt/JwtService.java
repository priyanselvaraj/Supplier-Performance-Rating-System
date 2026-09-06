package com.supplier.sprsystem.security.jwt;

import org.springframework.security.core.Authentication;

public interface JwtService {

    String generateJwtToken(Authentication authentication);

    String generateTokenFromUsername(String username);

    String getUserNameFromJwtToken(String token);

    boolean validateJwtToken(String authToken);
}
