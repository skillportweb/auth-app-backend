package com.substring.auth.auth_app_backend.auth.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookiesService {

    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String cookieSameSite;
    
    private final Logger logger =
            LoggerFactory.getLogger(CookiesService.class);

    public CookiesService(
            @Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
            @Value("${security.jwt.cookie-domain}") String cookieDomain,
            @Value("${security.jwt.cookie-same-site}") String cookieSameSite) {

        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
    }

    public String getRefreshTokenCookieName() {
		return refreshTokenCookieName;
	}

	// Create method to attach cookie to response
    public void attachRefreshCookie(
            HttpServletResponse response,
            String value,
            int maxAge) {
       logger.info("Attaching cookie with name: {}, value: {}, maxAge: {}", refreshTokenCookieName, value, maxAge);
        var responseCookieBuilder = ResponseCookie
                .from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = responseCookieBuilder.build();
        logger.info("Set-Cookie header: {}", responseCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
    //clear refresh cookie
    
    public void clearRefreshCookie(HttpServletResponse response) {

        var builder = ResponseCookie
                .from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = builder.build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                responseCookie.toString()
        );
    }
    
    
    // Prevent browser/proxy from caching authentication response
    public void addNoStoreHeaders(
            HttpServletResponse response) {

        response.setHeader(
                HttpHeaders.CACHE_CONTROL,
                "no-store"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );
    }

}