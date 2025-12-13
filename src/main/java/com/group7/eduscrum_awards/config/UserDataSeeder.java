package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * THIS CLASS SHOULD BE USED ONLY IN DEVELOPMENT OR TESTING ENVIRONMENTS!
 * 
 * This class seeds the database with an initial admin user if one does not already exist.
 */
@Component
public class UserDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedAdmin();
    }

    private void seedAdmin() {
        String adminEmail = "admintest@eduscrum.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            
            User admin = new User(
                "Super Administrator",
                adminEmail,
                passwordEncoder.encode("admin123"),
                Role.ADMIN
            );

            userRepository.save(admin);
            System.out.println("Admin User seeded: " + adminEmail);
        } else {
            System.out.println("Admin User already exists.");
        }
    }
}