package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Award;
import com.group7.eduscrum_awards.model.enums.AwardScope;
import com.group7.eduscrum_awards.model.enums.AwardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for transferring Award data. */
@NoArgsConstructor
@Getter
@Setter
public class AwardDTO {
    private Long id;
    private String name;
    private String description;
    private int points;
    private String badgeIcon;
    private AwardType type;
    private AwardScope scope;
    private Long courseId; // Null if global

    public AwardDTO(Award award) {
        this.id = award.getId();
        this.name = award.getName();
        this.description = award.getDescription();
        this.points = award.getPoints();
        this.badgeIcon = award.getBadgeIcon();
        this.type = award.getType();
        this.scope = award.getScope();
        if (award.getCourse() != null) {
            this.courseId = award.getCourse().getId();
        }
    }
}