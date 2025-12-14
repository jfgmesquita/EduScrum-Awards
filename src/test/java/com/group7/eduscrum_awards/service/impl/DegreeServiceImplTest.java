package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.dto.DegreeUpdateDTO;
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
import java.util.Arrays;
import java.util.List;
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

    @Test
    @DisplayName("getAllDegrees | Should return all degrees mapped to DTOs")
    void testGetAllDegrees_Success() {

        List<Degree> degreeList = Arrays.asList(existingDegree, otherDegree);
        when(degreeRepository.findAll()).thenReturn(degreeList);

        List<DegreeDTO> result = degreeService.getAllDegrees();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(existingDegree.getName(), result.get(0).getName()); // Validates mapping
        
        verify(degreeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateDegree | Should update name successfully")
    void testUpdateDegree() {

        Long id = 1L;
        Degree degree = new Degree("Old Name");
        DegreeUpdateDTO dto = new DegreeUpdateDTO();
        dto.setName("New Name");

        when(degreeRepository.findById(id)).thenReturn(Optional.of(degree));
        when(degreeRepository.save(any(Degree.class))).thenAnswer(i -> i.getArguments()[0]);

        DegreeDTO result = degreeService.updateDegree(id, dto);

        assertEquals("New Name", result.getName());
    }

    @Test
    @DisplayName("getDegreeById | Should return degree DTO when found")
    void testGetDegreeById_Success() {
        Long id = 1L;
        when(degreeRepository.findById(id)).thenReturn(Optional.of(existingDegree));

        DegreeDTO result = degreeService.getDegreeById(id);

        assertNotNull(result);
        assertEquals(existingDegree.getName(), result.getName());
        assertEquals(id, result.getId());
        
        verify(degreeRepository).findById(id);
    }

    @Test
    @DisplayName("getDegreeById | Should throw ResourceNotFoundException when not found")
    void testGetDegreeById_Failure_NotFound() {
        Long id = 99L;
        when(degreeRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> degreeService.getDegreeById(id)
        );

        assertEquals("Degree not found: " + id, exception.getMessage());
        
        verify(degreeRepository).findById(id);
    }
}