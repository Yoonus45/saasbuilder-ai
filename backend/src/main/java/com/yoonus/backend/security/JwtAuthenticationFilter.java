package com.yoonus.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService) {

        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request for path: {}", request.getRequestURI());

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);
                logger.debug("Token found in Authorization header");

                // Validate token first
                if (!jwtUtil.validateToken(token)) {
                    logger.warn("Token validation failed");
                    sendUnauthorizedResponse(response, "Invalid or expired JWT token");
                    return;
                }

                String email = jwtUtil.extractEmail(token);
                logger.debug("Extracted email from token: {}", email);

                if (email != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    try {
                        logger.debug("Loading user details for email: {}", email);
                        UserDetails userDetails =
                                userDetailsService.loadUserByUsername(email);
                        logger.debug("User details loaded successfully");

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request));

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                        logger.info("Authentication set in SecurityContextHolder for user: {}", email);
                    } catch (Exception ex) {
                        logger.error("Failed to load user details for email: {}", email, ex);
                        throw ex;
                    }
                } else {
                    logger.warn("Email is null or authentication already set. Email: {}, Existing Auth: {}", 
                            email, SecurityContextHolder.getContext().getAuthentication());
                }
            } else {
                logger.debug("No Authorization header or does not start with Bearer");
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Clear any partial authentication
            SecurityContextHolder.clearContext();
            logger.error("Authentication error", e);
            sendUnauthorizedResponse(response, "Authentication failed: " + e.getMessage());
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}