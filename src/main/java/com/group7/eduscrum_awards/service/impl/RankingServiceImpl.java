package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.dto.rankings.CourseRankingDTO;
import com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO;
import com.group7.eduscrum_awards.dto.rankings.StudentScoreDTO;
import com.group7.eduscrum_awards.dto.rankings.TeamMemberScoreDTO;
import com.group7.eduscrum_awards.dto.rankings.TeamRankingDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.repository.projection.StudentScoreSummary;
import com.group7.eduscrum_awards.repository.projection.TeamScoreSummary;
import com.group7.eduscrum_awards.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingServiceImpl implements RankingService {

    private final AwardAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for RankingServiceImpl.
     * 
     * @param assignmentRepository
     */
    @Autowired
    public RankingServiceImpl(AwardAssignmentRepository assignmentRepository, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets the student ranking for a specific degree.
     * 
     * @param degreeId the ID of the degree
     * @return List of RankingItemDTO representing student rankings.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingItemDTO> getStudentRanking(Long degreeId) {
        return assignmentRepository.findStudentRankingByDegree(degreeId);
    }

    /**
     * Gets the team ranking for a specific course.
     * 
     * @param courseId the ID of the course
     * @return List of RankingItemDTO representing team rankings.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingItemDTO> getTeamRanking(Long courseId) {
        return assignmentRepository.findTeamRankingByCourse(courseId);
    }

    /**
     * Gets the dashboard rankings for a specific student.
     * 
     * @param studentId the ID of the student
     * @return StudentDashboardRankingDTO containing individual and team rankings.
     */
    @Override
    @Transactional(readOnly = true)
    public StudentDashboardRankingDTO getStudentDashboardRankings(Long studentId) {
        
        // Fetch Student from repository
        Student student = (Student) userRepository.findById(studentId)
                .filter(u -> u instanceof Student)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        if (student.getDegree() == null) {
            throw new ResourceNotFoundException("Student is not enrolled in any degree.");
        }

        StudentDashboardRankingDTO response = new StudentDashboardRankingDTO();

        // Individual Rankings by Degree
        List<StudentScoreSummary> degreeSummaries = assignmentRepository.findStudentRankingsByDegreeId(student.getDegree().getId());
        
        List<StudentScoreDTO> individualRankings = new ArrayList<>();
        int rank = 1;
        for (StudentScoreSummary s : degreeSummaries) {
            individualRankings.add(new StudentScoreDTO(rank++, s.getStudentId(), s.getStudentName(), s.getTotalScore()));
        }
        response.setIndividualRankings(individualRankings);

        // Team Rankings by Course
        List<CourseRankingDTO> courseRankings = new ArrayList<>();
        
        for (Course course : student.getCourses()) {
            CourseRankingDTO courseDTO = new CourseRankingDTO();
            courseDTO.setCourseId(course.getId());
            courseDTO.setCourseName(course.getName());

            // Fetch Team Rankings for this Course
            List<TeamScoreSummary> teamSummaries = assignmentRepository.findTeamRankingsByCourseId(course.getId());
            List<TeamRankingDTO> teamRankings = new ArrayList<>();
            int teamRank = 1;
            
            for (TeamScoreSummary t : teamSummaries) {

                List<StudentScoreSummary> members = assignmentRepository.findTeamMemberScores(t.getTeamId());
                
                List<TeamMemberScoreDTO> membersDTO = members.stream()
                        .map(m -> new TeamMemberScoreDTO(m.getStudentId(), m.getStudentName(), m.getTotalScore()))
                        .collect(Collectors.toList());

                teamRankings.add(new TeamRankingDTO(
                    teamRank++, 
                    t.getTeamId(), 
                    t.getTeamName(), 
                    t.getTotalScore(), 
                    membersDTO.size(),
                    membersDTO
                ));
            }
            courseDTO.setRankings(teamRankings);
            courseRankings.add(courseDTO);
        }
        
        response.setTeamRankingsByCourse(courseRankings);
        return response;
    }
}