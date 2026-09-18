package com.substring.auth.auth_app_backend.auth.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.substring.auth.auth_app_backend.auth.services.impl.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.substring.auth.auth_app_backend.auth.entities.Role;
import com.substring.auth.auth_app_backend.auth.helpers.UserHelper;
import com.substring.auth.auth_app_backend.auth.repositories.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService ,UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    
    private final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)

            throws ServletException, IOException {

    	 String header = request.getHeader("Authorization");
         logger.info("Authorization header : {}", header);

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {

                Jws<Claims> claims = jwtService.parse(token);
                Claims payload = claims.getPayload();
                //check for access token
                if (!jwtService.isAccessToken(token)) {
                	//message pass kar h
                	filterChain.doFilter(request, response);
					return;
				}
                
                String userId = payload.getSubject();
                UUID userUuid = UserHelper.parseUUID(userId);
                
                userRepository.findById(userUuid)
                .ifPresent(user -> {
                	
                	if (user.isEnable()) {
                		  List<GrantedAuthority> authorities =
                                  user.getRoles() == null
                                      ? List.of()
                                      : user.getRoles().stream()
                                          .map((Role role) ->
                                              new SimpleGrantedAuthority(role.getName())
                                          )
                                          .collect(Collectors.toList());

                              // Create Authentication object
                              UsernamePasswordAuthenticationToken authenticationToken =
                                  new UsernamePasswordAuthenticationToken(
                                      user.getEmail(),
                                      null,
                                      authorities
                                  );

                              // Set request details
                              authenticationToken.setDetails(
                                  new WebAuthenticationDetailsSource()
                                      .buildDetails(request)
                              );

                              // Set Authentication into SecurityContext
                              SecurityContextHolder
                                  .getContext()
                                  .setAuthentication(authenticationToken);
					}

                  
                    
                    
                });
                

                System.out.println("Token valid: " + claims.getPayload());

            } catch (ExpiredJwtException e) {
               request.setAttribute("error", "Token Expired");
//                e.printStackTrace();

            } catch (Exception e) {
            	request.setAttribute("error", "Invalid Token");
//                e.printStackTrace();

            }

        }

        filterChain.doFilter(request, response);

    }
    
    protected boolean shoulNotFilter(HttpServletRequest request) throws ServletException{
    	return request.getRequestURI().startsWith("/api/v1/auth");
    }

}