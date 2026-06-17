package authservice.auth.application;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        String email,
        String keycloakId,
        LocalDateTime createdAt
) {
}
