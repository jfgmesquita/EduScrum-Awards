package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.model.enums.Role;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
            
            .authorizeHttpRequests(authz -> authz
                
                // PUBLIC ENDPOINTS - Allow login and registration
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll() // Login & auth endpoints

                // TEACHER-ONLY ENDPOINTS
                .requestMatchers(HttpMethod.POST, "/api/v1/courses/{courseId}/projects").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/courses/{courseId}/awards").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/{projectId}/teams").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/teams/{teamId}/members").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/awards/{awardId}/assign").hasRole(Role.TEACHER.name())

                // STUDENT-ONLY ENDPOINTS
                .requestMatchers(HttpMethod.POST, "/api/v1/projects/{projectId}/sprints").hasRole(Role.STUDENT.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/sprints/{sprintId}/tasks").hasRole(Role.STUDENT.name())
                .requestMatchers(HttpMethod.PATCH, "/api/v1/tasks/{taskId}/assign").hasRole(Role.STUDENT.name())

                // ADMIN-ONLY ENDPOINTS
                .requestMatchers(HttpMethod.GET, "/api/v1/users/students").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/users/teachers").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/degrees/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/courses/**").hasRole(Role.ADMIN.name())
                
                // SECURE EVERYTHING ELSE
                .anyRequest().authenticated()
            )
            
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Configures CORS to allow requests from the Angular frontend. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow the frontend origin (Angular default port) 
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); 
        
        // Allow standard HTTP methods used in REST APIs
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Allow necessary headers for JWT authentication
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Apply the CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}