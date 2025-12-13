package com.group7.eduscrum_awards.repository.projection;

/** Projection interface for summarizing a team's total score. */
public interface TeamScoreSummary {
    Long getTeamId();
    String getTeamName();
    Long getTotalScore();
}