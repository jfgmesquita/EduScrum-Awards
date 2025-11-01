package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;    
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DegreeServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of the DegreeRepository.
 */
@ExtendWith(MockitoExtension.class)
class DegreeServiceImplTest {

    @Mock
    private DegreeRepository degreeRepository;

    @InjectMocks
    private DegreeServiceImpl degreeService;

    private DegreeCreateDTO createDTO;
    private Degree existingDegree;

    /** Sets up common test data before each @Test method is executed */
    @BeforeEach
    void setUp() {
        // This DTO simulates the data coming from the controller
        createDTO = new DegreeCreateDTO();
        createDTO.setName("Test Degree");

        // This object simulates a record already in the database
        existingDegree = new Degree("Test Degree");
        existingDegree.setId(1L);
    }

    /**
     * Test Scenario 1: Successful registration of a new degree.
     * Verifies that the service correctly saves a new degree when the name is not a duplicate.
     */
    @Test
    @DisplayName("Should register degree successfully when name is unique")
    void testRegisterDegree_Success() {

        when(degreeRepository.findByName("Test Degree")).thenReturn(Optional.empty());
        when(degreeRepository.save(any(Degree.class))).thenReturn(existingDegree);

        DegreeDTO result = degreeService.registerDegree(createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Degree", result.getName());
        
        verify(degreeRepository, times(1)).findByName("Test Degree");
        verify(degreeRepository, times(1)).save(any(Degree.class));
    }

    /**
     * Test Scenario 2: Attempting to register a duplicate degree.
     * Verifies that the service throws a DuplicateResourceException when the name already exists.
     */
    @Test
    @DisplayName("Should throw DuplicateResourceException when name already exists")
    void testRegisterDegree_Failure_DuplicateName() {

        when(degreeRepository.findByName("Test Degree")).thenReturn(Optional.of(existingDegree));

        DuplicateResourceException exceptionThrown = assertThrows(
            DuplicateResourceException.class, // The exception type expected
            () -> {
                degreeService.registerDegree(createDTO); // The code that should throw it
            }
        );
        assertEquals("A Degree with the name 'Test Degree' already exists.", exceptionThrown.getMessage());

        verify(degreeRepository, times(1)).findByName("Test Degree");
        verify(degreeRepository, never()).save(any(Degree.class));
    }
}