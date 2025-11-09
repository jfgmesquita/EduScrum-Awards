package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main configuration class for Spring Security.
 * This class now configures stateless, token-based authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /** Configures the security filter chain. */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
            
            .authorizeHttpRequests(authz -> authz
                
                // PUBLIC ENDPOINTS
                // Allow login and registration
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                // ADMIN-ONLY ENDPOINTS
                .requestMatchers("/api/v1/degrees/**").hasRole(Role.ADMIN.name())
                .requestMatchers("/api/v1/courses/**").hasRole(Role.ADMIN.name()) // Admin can manage all courses

                // TEACHER-ONLY ENDPOINTS (Most specific rules go first)
                .requestMatchers(HttpMethod.POST, "/api/v1/courses/{courseId}/projects").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/{projectId}/teams").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/teams/{teamId}/members").hasRole(Role.TEACHER.name())

                // STUDENT-ONLY ENDPOINTS
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/{projectId}/sprints").hasRole(Role.STUDENT.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/sprints/{sprintId}/tasks").hasRole(Role.STUDENT.name())
                
                // --- SECURE EVERYTHING ELSE ---
                .anyRequest().authenticated()
            )
            
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}