package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.stats.*;

public interface StatsService {
    GlobalStatsDTO getGlobalStats();
    DegreeStatsDTO getDegreeStats(Long degreeId);
    CourseStatsDTO getCourseStats(Long courseId);
    TeacherStatsDTO getTeacherStats(Long teacherId);
}