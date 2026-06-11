package userservice.application.dto;

import userservice.domain.model.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u){
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getPhone(),
                u.getCreatedAt()
        );
    }
}
