package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Award;
import com.group7.eduscrum_awards.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Award} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and exposes finders to look up awards by their name and associated course.
 */
@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {

    /**
     * Finds an award by its name.
     * 
     * @param name The name of the award.
     * @return an {@link Optional} containing the award if a match is found.
     */
    Optional<Award> findByName(String name);

    /**
     * Finds all awards associated with a specific course.
     * 
     * @param course The Course entity.
     * @return A list of awards linked to the given course.
     */
    List<Award> findAllByCourse(Course course);

    /**
     * Finds all awards that are not associated with any course.
     * 
     * @return A list of awards with no associated course.
     */
    List<Award> findAllByCourseIsNull();
}