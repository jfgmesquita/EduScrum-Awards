package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.service.DegreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST Controller for managing Degrees.
 * This class exposes endpoints for the frontend to interact with.
 */
@RestController
@RequestMapping("/api/v1/degrees")
public class DegreeController {

    private final DegreeService degreeService;

    @Autowired
    public DegreeController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    /**
     * Endpoint to register a new Degree.
     * Accessible via: POST http://localhost:8080/api/v1/degrees
     *
     * @param degreeCreateDTO The data for the new Degree, passed in the request body.
     * @return A ResponseEntity containing the created DegreeDTO and HTTP status 201.
     * @Valid: Triggers the validation rules (@NotBlank, @Size) in the DTO.
     * @RequestBody: Converts the incoming JSON into a DegreeCreateDTO object.
     */
    @PostMapping
    public ResponseEntity<DegreeDTO> registerDegree(@Valid @RequestBody DegreeCreateDTO degreeCreateDTO) {
        
        DegreeDTO createdDegree = degreeService.registerDegree(degreeCreateDTO);
        // We return HttpStatus.CREATED (201) which is the standard for a successful POST operation.
        return new ResponseEntity<>(createdDegree, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint to assign an existing Student to an existing Degree.
     * Accessible via: POST http://localhost:8080/api/v1/degrees/{degreeId}/students/{studentId}
     *
     * @param degreeId The ID of the Degree.
     * @param studentId The ID of the Student to be assigned.
     * @return A ResponseEntity containing the updated DegreeDTO and HTTP status 200.
     */
    @PostMapping("/{degreeId}/students/{studentId}")
    public ResponseEntity<DegreeDTO> addStudentToDegree(@PathVariable Long degreeId, @PathVariable Long studentId) {
        
        DegreeDTO updatedDegree = degreeService.addStudentToDegree(degreeId, studentId);
        // We return HttpStatus.OK (200) which indicates the request was successful.
        return new ResponseEntity<>(updatedDegree, HttpStatus.OK);
    }
}
