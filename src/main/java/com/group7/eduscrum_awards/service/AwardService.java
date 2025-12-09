package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.AwardAssignmentRequestDTO;
import com.group7.eduscrum_awards.dto.AwardCreateDTO;
import com.group7.eduscrum_awards.dto.AwardDTO;
import java.util.List;

/** Service Interface for Award operations. */
public interface AwardService {

    /**
     * Creates a custom award for a specific course.
     * 
     * @param courseId ID of the course.
     * @param createDTO Data for creating the award.
     * @return The created AwardDTO.
     * @throws IllegalArgumentException if the course does not exist. 
     */
    AwardDTO createCustomAward(Long courseId, AwardCreateDTO createDTO);

    /**
     * Assigns an award to a student or all members of a team.
     * Returns a list of assignments (because one team = multiple students).
     * 
     * @param awardId ID of the award to assign.
     * @param requestDTO Data for the assignment request.
     * @throws IllegalArgumentException if the award, student, team, or project does not exist.
     */
    void assignAward(Long awardId, AwardAssignmentRequestDTO requestDTO);

    /**
     * Lists all available awards for a course (Global + Course-Specific).
     * 
     * @param courseId ID of the course.
     * @return List of AwardDTOs available for the course.
     * @throws IllegalArgumentException if the course does not exist.
     */
    List<AwardDTO> getAvailableAwards(Long courseId);

    /**
     * Retrieves all awards assigned to a specific student.
     * 
     * @param studentId ID of the student.
     * @return List of StudentAwardDTOs assigned to the student.
     * @throws IllegalArgumentException if the student does not exist.
     */
    List<com.group7.eduscrum_awards.dto.StudentAwardDTO> getStudentAwards(Long studentId);
}