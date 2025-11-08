package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.model.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main configuration class for Spring Security.
 * This class defines how security is handled (password encoding, URL protection, etc.).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the PasswordEncoder bean.
     *
     * @return A PasswordEncoder instance that Spring can inject anywhere.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * This is where we define which URLs are public and which are protected.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Temporarily disable CSRF for our API endpoints (common for stateless APIs)
            .csrf(csrf -> csrf.disable()) 
            // Set session management to stateless (no sessions)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(authz -> authz
            
                // Allow user registration for everyone
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                
                // Lock down the Degree endpoints to ADMINS only
                .requestMatchers("/api/v1/degrees/**").hasRole(Role.ADMIN.name())
                
                // Lock down the Course creation endpoint to TEACHERS only
                .requestMatchers(HttpMethod.POST, "/api/v1/courses/{courseId}/projects").hasRole(Role.TEACHER.name())
                .requestMatchers("/api/v1/courses/**").hasRole(Role.ADMIN.name())
                
                // Lock down the Project Teams creation endpoint to TEACHERS only
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/{projectId}/teams").hasRole(Role.TEACHER.name())
                
                // Lock down the Team Member assignment endpoint to TEACHERS only
                .requestMatchers(HttpMethod.POST, "/api/v1/teams/{teamId}/members").hasRole(Role.TEACHER.name())
                
                // Secure everything else
                .anyRequest().authenticated()
            );

        return http.build();
    }
}