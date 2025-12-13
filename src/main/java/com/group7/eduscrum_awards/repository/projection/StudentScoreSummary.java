package com.group7.eduscrum_awards.repository.projection;

/** Projection interface for summarizing a student's total score. */
public interface StudentScoreSummary {
    Long getStudentId();
    String getStudentName();
    Long getTotalScore();
}