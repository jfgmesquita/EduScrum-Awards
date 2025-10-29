package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Degree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Degree} entities.
 * 
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and exposes a finder to look up degrees by their name.
 */
@Repository
public interface DegreeRepository extends JpaRepository<Degree, Long> {

    /**
     * Find a degree by its unique name.
     *
     * @param name the degree name to search for
     * @return an {@link Optional} containing the matching {@link Degree} when
     * present, or an empty Optional if no match is found
     */
    Optional<Degree> findByName(String name);
}
