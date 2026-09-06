package com.supplier.sprsystem.security;

import com.supplier.sprsystem.security.jwt.AuthEntryPointJwt;
import com.supplier.sprsystem.security.jwt.AuthTokenFilter;
import com.supplier.sprsystem.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          AuthEntryPointJwt unauthorizedHandler,
                          AuthTokenFilter authTokenFilter,
                          ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authTokenFilter = authTokenFilter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "X-Request-ID", "X-API-KEY", "X-API-Key", "X-SPRS-Signature", "X-SPRS-Event", "X-SPRS-Timestamp"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization", "X-Request-ID", "Content-Disposition", "X-SPRS-Signature"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/health", "/api/v1/health", "/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**",
                                "/error"
                        ).permitAll()

                        // Role-based Endpoints
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                        // Integration Admin Management (Phase 16)
                        .requestMatchers("/api/v1/admin/integrations/**").hasRole("ADMIN")

                        // External API Endpoints (Phase 16 - Secured via API Key scopes or Admin/Manager role)
                        .requestMatchers("/api/v1/external/**").authenticated()

                        // Supplier Mutations (ADMIN and MANAGER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/suppliers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/suppliers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/suppliers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/suppliers/**").hasRole("ADMIN")

                        // Admin Only Mutations (Categories, Criteria, Evaluation Deletion)
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**", "/api/v1/criteria/**", "/api/v1/evaluation-criteria/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**", "/api/v1/criteria/**", "/api/v1/evaluation-criteria/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/categories/**", "/api/v1/criteria/**", "/api/v1/evaluation-criteria/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**", "/api/v1/criteria/**", "/api/v1/evaluation-criteria/**", "/api/v1/evaluations/**").hasRole("ADMIN")

                        // Criteria, Supplier & Category Reads (ADMIN and MANAGER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/suppliers/**", "/api/v1/categories/**", "/api/v1/criteria/**", "/api/v1/evaluation-criteria/**").hasAnyRole("ADMIN", "MANAGER")

                        // Ratings Endpoints (Phase 5)
                        .requestMatchers("/api/v1/ratings/**").hasAnyRole("ADMIN", "MANAGER")

                        // Dashboard & Analytics Endpoints (Phase 6)
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "MANAGER")

                        // Reports & Export Endpoints (Phase 7)
                        .requestMatchers("/api/v1/reports/**").hasAnyRole("ADMIN", "MANAGER")

                        // AI Intelligence Endpoints (Phase 11)
                        .requestMatchers("/api/v1/ai/**").hasAnyRole("ADMIN", "MANAGER")

                        // Notifications & Collaboration (Phase 12)
                        .requestMatchers("/api/v1/notifications/**").authenticated()
                        .requestMatchers("/api/v1/improvement-actions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/monitoring/**").hasAnyRole("ADMIN", "MANAGER")

                        // Supplier Portal & Self-Service (Phase 13)
                        .requestMatchers("/api/v1/supplier-portal/admin/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/supplier-portal/**").hasAnyRole("SUPPLIER", "ADMIN", "MANAGER")

                        // Default Authenticated
                        .anyRequest().authenticated()
                );

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
