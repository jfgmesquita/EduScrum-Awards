package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for sending User data to the client.
 * This DTO omits sensitive information like the password.
 */
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Long degreeId;
    private String degreeName;

    /**
     * Convenience constructor to map a User entity to this DTO.
     *
     * @param user The User entity to map from.
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();

        // If it's a student, populate degree info
        if (user instanceof Student) {
            Student student = (Student) user;
            if (student.getDegree() != null) {
                this.degreeId = student.getDegree().getId();
                this.degreeName = student.getDegree().getName();
            }
        }
    }
}