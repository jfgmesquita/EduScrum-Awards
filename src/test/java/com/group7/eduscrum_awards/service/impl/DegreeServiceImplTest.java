package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DegreeServiceImpl.
 * Mocks all repositories to test business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class DegreeServiceImplTest {

    @Mock
    private DegreeRepository degreeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DegreeServiceImpl degreeService;

    // Test Data
    private DegreeCreateDTO createDTO;
    private Degree existingDegree;
    private Degree otherDegree;   
    private Student existingStudent;
    private User adminUser; 

    /** Sets up common test data before each @Test method is executed */
    @BeforeEach
    void setUp() {
        // Data for registerDegree
        createDTO = new DegreeCreateDTO();
        createDTO.setName("Test Degree");

        existingDegree = new Degree("Test Degree");
        existingDegree.setId(1L);

        // Data for addStudentToDegree
        existingStudent = new Student("Test Student", "student@test.com", "pass");
        existingStudent.setId(4L);
        
        otherDegree = new Degree("Other Degree");
        otherDegree.setId(2L);
        
        adminUser = new User("Test Admin", "admin@test.com", "pass", Role.ADMIN);
        adminUser.setId(5L);
    }

    // Tests for registerDegree

    @Test
    @DisplayName("registerDegree | Should register successfully when name is unique")
    void testRegisterDegree_Success() {

        when(degreeRepository.findByName("Test Degree")).thenReturn(Optional.empty());
        doReturn(existingDegree).when(degreeRepository).save(notNull());

        DegreeDTO result = degreeService.registerDegree(createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        verify(degreeRepository, times(1)).findByName("Test Degree");
        verify(degreeRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("registerDegree | Should throw DuplicateResourceException when name exists")
    void testRegisterDegree_Failure_DuplicateName() {

        when(degreeRepository.findByName("Test Degree")).thenReturn(Optional.of(existingDegree));

        DuplicateResourceException exceptionThrown = assertThrows(
            DuplicateResourceException.class,
            () -> degreeService.registerDegree(createDTO)
        );
        assertEquals("A Degree with the name 'Test Degree' already exists.", exceptionThrown.getMessage());
        
        verify(degreeRepository, never()).save(any(Degree.class));
    }

    // Tests for addStudentToDegree

    @Test
    @DisplayName("addStudentToDegree | Should add student successfully")
    void testAddStudentToDegree_Success() {

        assertNull(existingStudent.getDegree());
        
        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existingStudent));

        DegreeDTO result = degreeService.addStudentToDegree(1L, 4L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(existingDegree.getStudents().contains(existingStudent));
        assertEquals(existingDegree, existingStudent.getDegree());
        
        verify(degreeRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(4L);
        verify(userRepository, times(1)).save(existingStudent); // Verify the student was saved
    }

    @Test
    @DisplayName("addStudentToDegree | Should throw ResourceNotFoundException when Degree not found")
    void testAddStudentToDegree_Failure_DegreeNotFound() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            degreeService.addStudentToDegree(1L, 4L);
        });

        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToDegree | Should throw ResourceNotFoundException when Student not found")
    void testAddStudentToDegree_Failure_StudentNotFound() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(userRepository.findById(4L)).thenReturn(Optional.empty()); // Student not found

        assertThrows(ResourceNotFoundException.class, () -> {
            degreeService.addStudentToDegree(1L, 4L);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToDegree | Should throw ResourceNotFoundException when User is not a Student")
    void testAddStudentToDegree_Failure_UserIsNotStudent() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(userRepository.findById(5L)).thenReturn(Optional.of(adminUser)); // Found an Admin, not a Student

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> degreeService.addStudentToDegree(1L, 5L)
        );

        assertEquals("Student not found with id: 5", exception.getMessage());
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToDegree | Should throw DuplicateResourceException when Student already in this Degree")
    void testAddStudentToDegree_Failure_Duplicate() {

        existingStudent.setDegree(existingDegree);
        
        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existingStudent));

        assertThrows(DuplicateResourceException.class, () -> {
            degreeService.addStudentToDegree(1L, 4L);
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToDegree | Should throw IllegalArgumentException when Student in another Degree")
    void testAddStudentToDegree_Failure_AlreadyInOtherDegree() {

        existingStudent.setDegree(otherDegree);
        
        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existingStudent));

        assertThrows(IllegalArgumentException.class, () -> {
            degreeService.addStudentToDegree(1L, 4L);
        });
        
        verify(userRepository, never()).save(any());
    }
}