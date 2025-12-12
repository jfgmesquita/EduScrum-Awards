package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.dto.DegreeUpdateDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.DegreeService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation that contains the logic for managing {@link Degree} entities.
 *
 * This class implements the {@link DegreeService} contract and performs validation, 
 * mapping between DTOs and entities, and persistence via {@link DegreeRepository}.
 * Methods in this service are transactional where modifications occur.
 */
@Service
public class DegreeServiceImpl implements DegreeService {

    private final DegreeRepository degreeRepository;
    private final UserRepository userRepository;

    @Autowired
    public DegreeServiceImpl(DegreeRepository degreeRepository, UserRepository userRepository) {
        this.degreeRepository = degreeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Register a new degree in the system.
     * 
     * Performs a uniqueness check by name and persists the new degree.
     * This method is transactional: on error the database changes are rolled back.
     *
     * @param degreeCreateDTO the DTO containing the information required to create the degree
     * @return a {@link DegreeDTO} representing the persisted degree (including generated id)
     * @throws DuplicateResourceException when a degree with the same name already exists
     */
    @Override
    @Transactional
    public DegreeDTO registerDegree(DegreeCreateDTO degreeCreateDTO) {

        // Check for duplicates
        degreeRepository.findByName(degreeCreateDTO.getName())
            .ifPresent(existingDegree -> {
                // If the Optional is 'present' (not empty), it means we found one.
                throw new DuplicateResourceException("A Degree with the name '" + degreeCreateDTO.getName() + "' already exists.");
            });

        // Map DTO to Entity
        Degree newDegree = new Degree(degreeCreateDTO.getName());

        // Save the Entity
        // The .save() method returns the entity as it was saved in the DB (now with an ID)
        Degree savedDegree = degreeRepository.save(newDegree);

        // Map Saved Entity to Response DTO
        return new DegreeDTO(savedDegree);
    }

    /**
     * Assigns an existing Student to an existing Degree.
     *
     * @param degreeId The ID of the Degree.
     * @param studentId The ID of the Student to assign.
     * @return A DTO of the updated Degree.
     */
    @Override
    @Transactional
    public DegreeDTO addStudentToDegree(Long degreeId, Long studentId) {
        
        // Find the Degree
        Degree degree = degreeRepository.findById(degreeId)
            .orElseThrow(() -> new ResourceNotFoundException("Degree not found with id: " + degreeId));

        // Find the User and validate it's a Student
        Student student = (Student) userRepository.findById(studentId)
            .filter(user -> user instanceof Student) // Ensure the User is a Student
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check if student is already in a degree
        if (student.getDegree() != null) {
            // Check if it's this same degree
            if(student.getDegree().equals(degree)) {
                 throw new DuplicateResourceException("Student with id " + studentId + " is already in this degree.");
            } else {
                 throw new IllegalArgumentException("Student with id " + studentId + " is already enrolled in another degree.");
            }
        }

        // Add the relationship
        degree.addStudent(student);

        // Save the 'Student' side to update the foreign key
        userRepository.save(student);

        // Return the Degree DTO
        return new DegreeDTO(degree);
    }

    /**
     * Retrieves all degrees in the system.
     * 
     * @return A list of {@link DegreeDTO} representing all degrees.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DegreeDTO> getAllDegrees() {
        return degreeRepository.findAll().stream().map(DegreeDTO::new).collect(Collectors.toList());
    }

    /**
     * Updates an existing degree's information.
     * 
     * Only non-null and non-blank fields in the DTO are updated.
     * This method is transactional: on error the database changes are rolled back.
     *
     * @param id the ID of the degree to update
     * @param dto the DTO containing the updated information
     * @return a {@link DegreeDTO} representing the updated degree
     * @throws ResourceNotFoundException when no degree with the given ID exists
     */
    @Override
    @Transactional
    public DegreeDTO updateDegree(Long id, DegreeUpdateDTO dto) {
        Degree degree = degreeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Degree not found: " + id));
        
        if (dto.getName() != null && !dto.getName().isBlank()) {
            degree.setName(dto.getName());
        }
        return new DegreeDTO(degreeRepository.save(degree));
    }
}
