package com.group7.eduscrum_awards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            
            .authorizeHttpRequests(authz -> authz
                // TEMPORARY to test our endpoints in Postman, for example
                // Allow ALL requests to any URL (e.g., /api/v1/degrees)
                .requestMatchers("/**").permitAll() 
                
                // IN THE FUTURE, we will change this to be specific, like:
                // .requestMatchers(HttpMethod.POST, "/api/v1/degrees").hasRole("ADMIN")
                // .requestMatchers("/api/v1/auth/**").permitAll() // Allow login/register
                // .anyRequest().authenticated() // Secure all other endpoints
            );

        return http.build();
    }
}