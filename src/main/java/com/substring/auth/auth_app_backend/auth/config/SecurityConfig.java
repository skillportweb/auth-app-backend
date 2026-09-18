package com.substring.auth.auth_app_backend.auth.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.substring.auth.auth_app_backend.dtos.ApiError;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationSuccessHandler successHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationSuccessHandler successHandler) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.successHandler = successHandler;
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // =====================================================
            // CSRF
            // =====================================================

            .csrf(AbstractHttpConfigurer::disable)

            // =====================================================
            // CORS
            // =====================================================

            .cors(Customizer.withDefaults())

            // =====================================================
            // SESSION MANAGEMENT
            // OAuth2 login requires temporary session
            // =====================================================

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )

            // =====================================================
            // AUTHORIZATION
            // =====================================================

            .authorizeHttpRequests(authorize -> authorize

                // Public URLs
                .requestMatchers(
                    AppConstants.AUTH_PUBLIC_URLS
                )
                .permitAll()

                    // User management APIs require ADMIN role
                    .requestMatchers(AppConstants.AUTH_ADMIN_URLS)
                    .hasRole(AppConstants.ADMIN_ROLE)

                // GET requests require GUEST role
                .requestMatchers(AppConstants.AUTH_PUBLIC_URLS)
                .hasRole(AppConstants.GUEST_ROLE)



                // All other requests require authentication
                .anyRequest()
                .authenticated()
            )

            // =====================================================
            // OAUTH2 LOGIN
            // =====================================================

            .oauth2Login(oauth2 -> oauth2

                .successHandler(successHandler)

                .failureHandler(
                    (request, response, exception) -> {

                        exception.printStackTrace();

                        response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                        );

                        response.setContentType(
                            "application/json"
                        );

                        ApiError apiError = ApiError.of(
                            HttpStatus.UNAUTHORIZED.value(),
                            "OAuth2 Login Failed",
                            exception.getMessage(),
                            request.getRequestURI()
                        );

                        ObjectMapper objectMapper =
                            new ObjectMapper();

                        response.getWriter().write(
                            objectMapper.writeValueAsString(
                                apiError
                            )
                        );
                    }
                )
            )

            // =====================================================
            // LOGOUT
            // =====================================================

            .logout(AbstractHttpConfigurer::disable)

            // =====================================================
            // EXCEPTION HANDLING
            // =====================================================

            .exceptionHandling(ex -> ex

                // -------------------------------------------------
                // 401 - Authentication required
                // -------------------------------------------------

                .authenticationEntryPoint(
                    (request, response, exception) -> {

                        exception.printStackTrace();

                        response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                        );

                        response.setContentType(
                            "application/json"
                        );

                        String message = exception.getMessage();

                        String error =
                            (String) request.getAttribute("error");

                        if (error != null) {
                            message = error;
                        }

                        ApiError apiError = ApiError.of(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Unauthorized Access",
                            message,
                            request.getRequestURI()
                        );

                        ObjectMapper objectMapper =
                            new ObjectMapper();

                        response.getWriter().write(
                            objectMapper.writeValueAsString(
                                apiError
                            )
                        );
                    }
                )

                // -------------------------------------------------
                // 403 - Access denied
                // -------------------------------------------------

                .accessDeniedHandler(
                    (request, response, exception) -> {

                        response.setStatus(
                            HttpServletResponse.SC_FORBIDDEN
                        );

                        response.setContentType(
                            "application/json"
                        );

                        String message =
                            exception.getMessage();

                        String error =
                            (String) request.getAttribute("error");

                        if (error != null) {
                            message = error;
                        }

                        ApiError apiError = ApiError.of(
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden Access",
                            message,
                            request.getRequestURI()
                        );

                        ObjectMapper objectMapper =
                            new ObjectMapper();

                        response.getWriter().write(
                            objectMapper.writeValueAsString(
                                apiError
                            )
                        );
                    }
                )
            )

            // =====================================================
            // JWT FILTER
            // =====================================================

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.front-end-url}") String corsUrls) {

        String[] urls = corsUrls
                .trim()
                .split("\\s*,\\s*");

        CorsConfiguration config =
                new CorsConfiguration();

        // Allowed Origins
        config.setAllowedOrigins(
            Arrays.asList(urls)
        );

        // Allowed Methods
        config.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH",
                "HEAD"
            )
        );

        // Allowed Headers
        config.setAllowedHeaders(
            Arrays.asList("*")
        );

        // Cookies / credentials
        config.setAllowCredentials(true);

        // Exposed Headers
        config.setExposedHeaders(
            Arrays.asList(
                "Authorization",
                "Set-Cookie"
            )
        );

        // CORS Source
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            config
        );

        return source;
    }
}