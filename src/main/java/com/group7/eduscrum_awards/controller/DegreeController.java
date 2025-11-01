package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.service.DegreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Degrees.
 * This class exposes endpoints for the frontend to interact with.
 */
@RestController
@RequestMapping("/api/v1/degrees") // Base URL for all endpoints in this controller
public class DegreeController {

    private final DegreeService degreeService;

    @Autowired
    public DegreeController(DegreeService degreeService) {
        // We inject the SERVICE INTERFACE, not the implementation
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
}
