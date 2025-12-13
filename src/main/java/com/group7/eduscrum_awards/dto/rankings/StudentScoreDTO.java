package com.group7.eduscrum_awards.dto.rankings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for individual student ranking items. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentScoreDTO {
    private int rank;
    private Long studentId;
    private String studentName;
    private Long totalScore;
}