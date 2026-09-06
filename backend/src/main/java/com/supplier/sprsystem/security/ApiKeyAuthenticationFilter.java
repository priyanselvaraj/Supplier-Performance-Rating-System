package com.supplier.sprsystem.security;

import com.supplier.sprsystem.model.entity.ApiKey;
import com.supplier.sprsystem.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // Only attempt API key auth if no authentication exists yet
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String apiKeyRaw = extractApiKey(request);
                if (StringUtils.hasText(apiKeyRaw)) {
                    String hash = hashKey(apiKeyRaw);
                    Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyHash(hash);

                    if (apiKeyOpt.isPresent()) {
                        ApiKey apiKey = apiKeyOpt.get();
                        if (apiKey.isValid()) {
                            // Update last used timestamp
                            apiKey.setLastUsedAt(LocalDateTime.now());
                            apiKeyRepository.save(apiKey);

                            // Build granted authorities
                            List<GrantedAuthority> authorities = new ArrayList<>();
                            authorities.add(new SimpleGrantedAuthority("ROLE_API_CLIENT"));
                            if (apiKey.getScopes() != null) {
                                apiKey.getScopes().forEach(scope ->
                                        authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.name()))
                                );
                            }

                            ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(
                                    apiKey.getName(),
                                    null,
                                    authorities,
                                    apiKey
                            );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("Successfully authenticated API Key: {} with scopes: {}", apiKey.getName(), apiKey.getScopes());
                        } else {
                            logger.warn("API Key with prefix {} is inactive or expired", apiKey.getKeyPrefix());
                        }
                    } else {
                        logger.debug("No active API Key found for provided key");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set API key authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        // 1. Check X-API-KEY header
        String apiKey = request.getHeader("X-API-KEY");
        if (StringUtils.hasText(apiKey)) {
            return apiKey.trim();
        }

        // 2. Check X-API-Key or x-api-key header
        apiKey = request.getHeader("X-API-Key");
        if (StringUtils.hasText(apiKey)) {
            return apiKey.trim();
        }

        // 3. Check Authorization header with ApiKey prefix
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader)) {
            if (authHeader.startsWith("ApiKey ")) {
                return authHeader.substring(7).trim();
            } else if (authHeader.startsWith("Bearer sprs_live_")) {
                return authHeader.substring(7).trim();
            }
        }

        return null;
    }

    public static String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
