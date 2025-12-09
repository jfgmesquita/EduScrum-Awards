package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.AwardScope;
import com.group7.eduscrum_awards.model.enums.AwardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an Award definition (Badge).
 * e.g., "Fast Hands", 10 points, Automatic.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "awards")
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int points;

    /**
     * The identifier for the badge icon (e.g., "fast-hands-icon").
     * If NULL (for manual teacher awards), the frontend should show a generic icon.
     */
    @Column(name = "badge_icon")
    private String badgeIcon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AwardType type; // MANUAL or AUTOMATIC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AwardScope scope; // STUDENT or TEAM

    /**
     * The course this award belongs to.
     * If NULL, it is a GLOBAL award (available to all courses).
     * If SET, it is a custom award created by a teacher for this specific course.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /** Convenience constructor. */
    public Award(String name, String description, int points, AwardType type, AwardScope scope, String badgeIcon, Course course) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.type = type;
        this.scope = scope;
        this.badgeIcon = badgeIcon;
        this.course = course;
    }

    /**
     * Equality is based on the identifier when it is set.
     * Two transient instances (with null id) are considered different.
     *
     * @param o object to compare
     * @return true if both objects are the same or have the same non-null id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Award award = (Award) o;
        return id != null && id.equals(award.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}