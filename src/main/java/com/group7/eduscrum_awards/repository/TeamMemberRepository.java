package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/** Repository for the TeamMember association entity. */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /**
     * Checks if a specific student is already a member of any team within a specific project.
     * This enforces the "one team per student per project" rule.
     *
     * @param student The student to check.
     * @param project The project to check within.
     * @return An Optional<TeamMember> if a membership exists.
     */
    Optional<TeamMember> findByStudentAndProject(Student student, Project project);
}