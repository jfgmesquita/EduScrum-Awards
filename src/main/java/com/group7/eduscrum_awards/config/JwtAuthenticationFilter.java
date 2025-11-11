package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * A custom filter that runs once per request.
 * This filter intercepts all incoming requests to check for a valid JWT.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // If no token exists, or it's not a Bearer token, pass to the next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token (e.g., "Bearer aG9sYS...")
        jwt = authHeader.substring(7); // 7 is the length of "Bearer "

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Token is invalid (expired, bad signature, etc.)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }


        // If token is valid, but user is not yet authenticated in Spring's context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Check if the token is still valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // Create an authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No need for credentials here
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Set this user as the authenticated user for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Pass the request to the next filter
        filterChain.doFilter(request, response);
    }
}