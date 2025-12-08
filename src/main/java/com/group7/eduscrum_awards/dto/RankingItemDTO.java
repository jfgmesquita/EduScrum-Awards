package com.group7.eduscrum_awards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data Transfer Object representing a ranking item for a student or team.*/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RankingItemDTO {
    private String name; // Name of the student or team
    private Double totalScore; // Total score accumulated
}