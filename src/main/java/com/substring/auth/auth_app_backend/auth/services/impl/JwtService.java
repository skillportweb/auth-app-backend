package com.substring.auth.auth_app_backend.auth.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.substring.auth.auth_app_backend.auth.entities.Role;
import com.substring.auth.auth_app_backend.auth.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtiSeconds;
    private final long refreshTtiSeconds;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtiSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtiSeconds,
            @Value("${security.jwt.issuer}") String issuer) {

        	if (secret == null || secret.length() < 64) {
			throw new IllegalArgumentException("Invalid secret");
		}
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtiSeconds = accessTtiSeconds;
        this.refreshTtiSeconds = refreshTtiSeconds;
        this.issuer = issuer;
    }
    
    //generate token: 
    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        List<String> roles = user.getRoles() == null ? List.of()
                : user.getRoles().stream().map(Role::getName).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtiSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }
    
 // generate refreshtoken.

    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtiSeconds)))
                .claim("typ", "refresh")
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }
    
    // parse the token
    
    public Jws<Claims> parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            throw e;
        }
    }
    
    public boolean isAccessToken(String token) {
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("typ"));
    }
    
    
    public boolean isRefreshToken(String token) {
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }
    
    public UUID getUserId(String token) {
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }
    
    public String getJti(String token) {
    	return parse(token).getPayload().getId();
    }
    
    public List<String> getRole(String token) {
        Claims c = parse(token).getPayload();
        return (List<String>) c.get("roles");
    }
    
    public String getEmail(String token) {
        Claims c = parse(token).getPayload();
        return (String) c.get("email");
    }

	public SecretKey getKey() {
		return key;
	}

	public long getAccessTtiSeconds() {
		return accessTtiSeconds;
	}

	public long getRefreshTtiSeconds() {
		return refreshTtiSeconds;
	}

	public String getIssuer() {
		return issuer;
	}
    
    
    
}