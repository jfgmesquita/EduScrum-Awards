package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the CourseService.
 * Handles the logic for managing courses.
 */
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DegreeRepository degreeRepository;
    private final UserRepository userRepository; 

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, 
                             DegreeRepository degreeRepository, 
                             UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.degreeRepository = degreeRepository;
        this.userRepository = userRepository;
    }

    /** Registers a new course for a specific degree. */
    @Override
    @Transactional
    public CourseDTO registerCourseForDegree(Long degreeId, CourseCreateDTO courseCreateDTO) {
        
        Degree degree = degreeRepository.findById(degreeId)
            .orElseThrow(() -> new ResourceNotFoundException("Degree not found with id: " + degreeId));

        courseRepository.findByNameAndDegree(courseCreateDTO.getName(), degree)
            .ifPresent(c -> {
                throw new DuplicateResourceException("A Course with the name '" + courseCreateDTO.getName() + "' already exists for this Degree.");
            });

        Course newCourse = new Course(courseCreateDTO.getName());
        degree.addCourse(newCourse);
        Course savedCourse = courseRepository.save(newCourse);
        return new CourseDTO(savedCourse);
    }

    /** Assigns an existing Teacher to an existing Course. */
    @Override
    @Transactional
    public CourseDTO addTeacherToCourse(Long courseId, Long teacherId) {

        // Find the Course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Find the Teacher (and verify it's a Teacher)
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
            .filter(user -> user instanceof Teacher) // Ensure the User is a Teacher
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        // Check if the association already exists
        if (course.getTeachers().contains(teacher)) {
            throw new DuplicateResourceException("Teacher with id " + teacherId + " is already assigned to this course.");
        }

        // Add the relationship
        teacher.addCourse(course);
        
        // Save (the @Transactional will persist the change)
        courseRepository.save(course);

        // Return the updated DTO
        return new CourseDTO(course);
    }

    /**
     * Enrolls an existing Student in an existing Course.
     *
     * @param courseId The ID of the course.
     * @param studentId The ID of the student to enroll.
     * @return A DTO of the updated Course.
     * @throws ResourceNotFoundException if the course or student is not found.
     * @throws DuplicateResourceException if the student is already enrolled.
     */
    @Override
    @Transactional
    public CourseDTO addStudentToCourse(Long courseId, Long studentId) {
        
        // Find the Course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Find the User and validate it's a Student
        Student student = (Student) userRepository.findById(studentId)
            .filter(user -> user instanceof Student)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check for duplicate assignment
        if (course.getStudents().contains(student)) {
            throw new DuplicateResourceException("Student with id " + studentId + " is already enrolled in this course.");
        }

        // Add the relationship
        student.addCourse(course); 
        
        // Save the "owning" side of this relationship
        userRepository.save(student);

        // Return the Course DTO
        return new CourseDTO(course);
    }

    /**
     * Retrieves all courses in the system.
     * 
     * @return A list of {@link CourseDTO} representing all courses.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(CourseDTO::new).collect(Collectors.toList());
    }
}