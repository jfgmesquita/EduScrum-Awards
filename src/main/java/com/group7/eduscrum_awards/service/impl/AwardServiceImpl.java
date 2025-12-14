package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.AwardAssignmentRequestDTO;
import com.group7.eduscrum_awards.dto.AwardCreateDTO;
import com.group7.eduscrum_awards.dto.AwardDTO;
import com.group7.eduscrum_awards.dto.StudentAwardDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.*;
import com.group7.eduscrum_awards.model.enums.AwardType;
import com.group7.eduscrum_awards.repository.*;
import com.group7.eduscrum_awards.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of AwardService for managing awards.
 * Handles creation, assignment, and retrieval of awards.
 */
@Service
public class AwardServiceImpl implements AwardService {

    private final AwardRepository awardRepository;
    private final AwardAssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public AwardServiceImpl(AwardRepository awardRepository, AwardAssignmentRepository assignmentRepository,
                            CourseRepository courseRepository, ProjectRepository projectRepository,
                            UserRepository userRepository, TeamRepository teamRepository) {
        this.awardRepository = awardRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Creates a custom award for a specific course.
     * 
     * @param courseId ID of the course.
     * @param createDTO Data for creating the award.
     * @return The created AwardDTO.
     * @throws ResourceNotFoundException if the course does not exist.
     */
    @Override
    @Transactional
    public AwardDTO createCustomAward(Long courseId, AwardCreateDTO createDTO) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Create the award (Manual, Student scope by default for custom awards, null icon)
        Award award = new Award(
            createDTO.getName(),
            createDTO.getDescription(),
            createDTO.getPoints(),
            AwardType.MANUAL,
            createDTO.getScope(),
            null,
            course
        );

        return new AwardDTO(awardRepository.save(award));
    }

    /**
     * Assigns an award to a student or all members of a team.
     * Returns a list of assignments (because one team = multiple students).
     */
    @Override
    @Transactional
    public void assignAward(Long awardId, AwardAssignmentRequestDTO request) {
        Award award = awardRepository.findById(awardId)
            .orElseThrow(() -> new ResourceNotFoundException("Award not found with id: " + awardId));

        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        // Assign to a Team
        if (request.getTeamId() != null) {
            Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));
            
            // Team must belong to the project
            if (!team.getProject().getId().equals(project.getId())) {
                throw new IllegalArgumentException("Team does not belong to the specified project.");
            }

            // Give the award to every member of the team, iterating over the team members (Set<TeamMember>)
            for (TeamMember member : team.getMembers()) {
                AwardAssignment assignment = new AwardAssignment(award, member.getStudent(), project, team);
                assignmentRepository.save(assignment);
            }
        
        // Or assign to an Individual Student
        } else if (request.getStudentId() != null) {
            Student student = (Student) userRepository.findById(request.getStudentId())
                .filter(user -> user instanceof Student)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

            AwardAssignment assignment = new AwardAssignment(award, student, project, null);
            assignmentRepository.save(assignment);

        } else {
            throw new IllegalArgumentException("Either studentId or teamId must be provided.");
        }
    }

    /**
     * Lists all available awards for a course (Global + Course-Specific).
     * 
     * @param courseId ID of the course.
     * @return List of AwardDTOs available for the course.
     * @throws ResourceNotFoundException if the course does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AwardDTO> getAvailableAwards(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Get global awards
        List<Award> globals = awardRepository.findAllByCourseIsNull();
        // Get course-specific awards
        List<Award> locals = awardRepository.findAllByCourse(course);

        // Combine lists
        return Stream.concat(globals.stream(), locals.stream())
                .map(AwardDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all awards assigned to a specific student.
     * 
     * @param studentId ID of the student.
     * @return List of StudentAwardDTOs assigned to the student.
     * @throws ResourceNotFoundException if the student does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentAwardDTO> getStudentAwards(Long studentId) {
        
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        return assignmentRepository.findAllByStudentIdOrderByAssignedAtDesc(studentId)
                .stream()
                .map(StudentAwardDTO::new)
                .collect(Collectors.toList());
    }
}