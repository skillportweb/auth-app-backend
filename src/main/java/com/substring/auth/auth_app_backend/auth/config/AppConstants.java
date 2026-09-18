package com.substring.auth.auth_app_backend.auth.config;

public class AppConstants {

    public static final String[] AUTH_PUBLIC_URLS = {

        // Auth APIs
        "/api/v1/auth/**",

        // Swagger / OpenAPI
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",

        // OAuth2
        "/oauth2/**",
        "/login/oauth2/**",

        // Login / Error
        "/login",
        "/login/**",
        "/error",
        "/error/**"
    };

    public static final String[] AUTH_ADMIN_URLS = {
           "/api/v1/users/**"
    };
    public static final String[] AUTH_GUEST_URL = {

    };

    public static final String ADMIN_ROLE = "ADMIN";

    public static final String GUEST_ROLE = "GUEST";

    private AppConstants() {
        // Utility class
    }
}