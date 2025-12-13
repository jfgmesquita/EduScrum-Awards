package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Developer information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperDTO {
    
    private Long teamMemberId; 
    private Long userId;       
    private String name;
    private String email;

    public DeveloperDTO(TeamMember member) {
        this.teamMemberId = member.getId();
        if (member.getStudent() != null) {
            this.userId = member.getStudent().getId();
            this.name = member.getStudent().getName();
            this.email = member.getStudent().getEmail();
        }
    }
}