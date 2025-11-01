package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.service.DegreeService;
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

    // Dependency Injection: We need the repository to talk to the DB
    private final DegreeRepository degreeRepository;

    @Autowired
    public DegreeServiceImpl(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    /**
     * Register a new degree in the system.
     * 
     * Performs a uniqueness check by name and persists the new degree.
     * This method is transactional: on error the database changes are rolled back.
     *
     * @param degreeCreateDTO the DTO containing the information required to create the degree
     * @return a {@link DegreeDTO} representing the persisted degree (including generated id)
     * @throws IllegalArgumentException when a degree with the same name already exists
     */
    @Override
    @Transactional
    public DegreeDTO registerDegree(DegreeCreateDTO degreeCreateDTO) {

        // Check for duplicates
        degreeRepository.findByName(degreeCreateDTO.getName())
            .ifPresent(existingDegree -> {
                // If the Optional is 'present' (not empty), it means we found one.
                throw new IllegalArgumentException("A Degree with the name '" + degreeCreateDTO.getName() + "' already exists.");
            });

        // Map DTO to Entity
        Degree newDegree = new Degree(degreeCreateDTO.getName());

        // Save the Entity
        // The .save() method returns the entity as it was saved in the DB (now with an ID)
        Degree savedDegree = degreeRepository.save(newDegree);

        // Map Saved Entity to Response DTO
        return new DegreeDTO(savedDegree);
    }
}
