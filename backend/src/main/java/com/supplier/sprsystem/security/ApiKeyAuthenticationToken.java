package com.supplier.sprsystem.security;

import com.supplier.sprsystem.model.entity.ApiKey;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private final ApiKey apiKey;

    public ApiKeyAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, ApiKey apiKey) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

    public ApiKeyAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.apiKey = null;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
