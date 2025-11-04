package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServiceImpl.
 * These tests isolate the service logic and mock the 
 * UserRepository and PasswordEncoder dependencies.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDTO createDTO;
    private User savedUser;
    private String hashedPassword;

    /** Sets up common test data before each test. */
    @BeforeEach
    void setUp() {
        // Simulate the DTO coming from the controller
        createDTO = new UserCreateDTO();
        createDTO.setName("Test User");
        createDTO.setEmail("test@user.com");
        createDTO.setPassword("plainpassword123");
        createDTO.setRole(Role.ADMIN);

        // Simulate the hashed password
        hashedPassword = "bcrypt-hashed-password-string";

        // Simulate the User entity as it would be saved in the DB
        savedUser = new User(
            createDTO.getName(),
            createDTO.getEmail(),
            hashedPassword,
            createDTO.getRole()
        );
        savedUser.setId(1L);
    }

    /**
     * Test Scenario 1: Successful registration of a new user.
     * Verifies email check, password hashing, and saving.
     */
    @Test
    @DisplayName("Should register user successfully when email is unique")
    void testRegisterUser_Success() {

        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainpassword123")).thenReturn(hashedPassword);
        doReturn(savedUser).when(userRepository).save(notNull());

        UserDTO result = userService.registerUser(createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@user.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository, times(1)).findByEmail("test@user.com");
        verify(passwordEncoder, times(1)).encode("plainpassword123");
        verify(userRepository, times(1)).save(notNull());
    }

    /**
     * Test Scenario 2: Attempting to register with a duplicate email.
     * Verifies that a DuplicateResourceException is thrown.
     */
    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void testRegisterUser_Failure_DuplicateEmail() {

        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(savedUser));

        DuplicateResourceException exceptionThrown = assertThrows(
            DuplicateResourceException.class,
            () -> {
                userService.registerUser(createDTO); // This line should throw
            }
        );
        assertEquals("A user with the email 'test@user.com' already exists.", exceptionThrown.getMessage());

        verify(userRepository, times(1)).findByEmail("test@user.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}