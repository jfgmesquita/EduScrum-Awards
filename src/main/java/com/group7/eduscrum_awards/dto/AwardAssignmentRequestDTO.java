package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** DTO for assigning an Award to a Student or Team. */
@Getter
@Setter
public class AwardAssignmentRequestDTO {

    @NotNull(message = "Project ID is required to establish context.")
    private Long projectId;

    // Optional: Only one of these should be set
    private Long studentId;
    private Long teamId;
}