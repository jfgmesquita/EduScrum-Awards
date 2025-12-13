package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Course;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) for the {@link Course} entity.
 *
 * This DTO is used to expose basic course information (id and name)
 * via the REST API, typically as a response after creation or in lists.
 */
@NoArgsConstructor
@Getter
@Setter
public class CourseDTO {

    private Long id;
    private String name;
    private Long degreeId;
    private String degreeName;

    /**
     * Create a DTO from the given entity.
     *
     * @param course the source {@link Course} entity (must not be null)
     */
    public CourseDTO(Course course) {
        this.id = course.getId();
        this.name = course.getName();

        if (course.getDegree() != null) {
            this.degreeId = course.getDegree().getId();
            this.degreeName = course.getDegree().getName();
        }
    }
}