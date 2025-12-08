package com.group7.eduscrum_awards.config;

import com.group7.eduscrum_awards.model.Award;
import com.group7.eduscrum_awards.model.enums.AwardScope;
import com.group7.eduscrum_awards.model.enums.AwardType;
import com.group7.eduscrum_awards.repository.AwardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Loads the default/global awards into the database on startup.
 */
@Component
public class AwardDataSeeder implements CommandLineRunner {

    private final AwardRepository awardRepository;

    @Autowired
    public AwardDataSeeder(AwardRepository awardRepository) {
        this.awardRepository = awardRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadGlobalAwards();
    }

    private void loadGlobalAwards() {
        // Default/global awards to be seeded
        List<Award> defaultAwards = Arrays.asList(
            // Automatic Awards
            new Award(
                "Multitasker",
                "Complete 3 or more tasks in a single day.",
                2,
                AwardType.AUTOMATIC,
                AwardScope.STUDENT,
                "multitasker-badge", // Name of the badge image/resource for the frontend
                null // Course null = Global
            ),
            new Award(
                "Nice Job!",
                "Complete 1 task.",
                1,
                AwardType.AUTOMATIC,
                AwardScope.STUDENT,
                "nice-job-badge",
                null
            ),
            new Award(
                "Hard Worker",
                "Complete more than 7 tasks in a week.",
                2,
                AwardType.AUTOMATIC,
                AwardScope.STUDENT,
                "hard-worker-badge",
                null
            ),
            new Award(
                "Fast Hands",
                "Complete a task on the same day it was assigned.",
                1,
                AwardType.AUTOMATIC,
                AwardScope.STUDENT,
                "fast-hands-badge",
                null
            ),

            // Manual Awards
            new Award(
                "Most Valuable Member",
                "Distinguished member of the project.",
                3,
                AwardType.MANUAL,
                AwardScope.STUDENT,
                "mvm-badge",
                null
            ),
            new Award(
                "Most Valuable Team",
                "Best performing team.",
                3,
                AwardType.MANUAL,
                AwardScope.TEAM,
                "mvt-badge",
                null
            ),
            new Award(
                "Innovator",
                "Innovative solution to a task.",
                2,
                AwardType.MANUAL,
                AwardScope.STUDENT,
                "innovator-badge",
                null
            )
        );

        // Seed the awards if they don't already exist
        for (Award award : defaultAwards) {
            if (awardRepository.findByName(award.getName()).isEmpty()) {
                awardRepository.save(award);
                System.out.println("Seeded Award: " + award.getName());
            }
        }
    }
}