package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Task} entities.
 *
 * Provides basic CRUD operations via JpaRepository.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // We can custom queries later if needed (e.g., findAllBySprint(Sprint sprint))
}