package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Course} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and exposes a finder to look up courses by their name.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its name and its associated degree.
     *
     * @param name The name of the course.
     * @param degree The Degree entit.
     * @return an {@link Optional} containing the course if a match is found.
     */
    Optional<Course> findByNameAndDegree(String name, Degree degree);

    /**
     * Counts courses by degree ID.
     * 
     * @param degreeId
     * @return the count of courses in the specified degree
     */
    long countByDegreeId(Long degreeId);

    /**
     * Counts students in a course.
     * 
     * @param courseId
     * @return the count of students in the specified course
     */
    @Query("SELECT COUNT(s) FROM Course c JOIN c.students s WHERE c.id = :courseId")
    long countStudentsByCourseId(@Param("courseId") Long courseId);

    /**
     * Counts teachers in a course.
     * 
     * @param courseId
     * @return the count of teachers in the specified course
     */
    @Query("SELECT COUNT(t) FROM Course c JOIN c.teachers t WHERE c.id = :courseId")
    long countTeachersByCourseId(@Param("courseId") Long courseId);

    /**
     * Counts courses taught by a specific teacher.
     * 
     * @param teacherId
     * @return the count of courses taught by the specified teacher
     */
    @Query("SELECT COUNT(c) FROM Course c JOIN c.teachers t WHERE t.id = :teacherId")
    long countCoursesByTeacherId(@Param("teacherId") Long teacherId);
}