package authserver.application.dto;

public record LoginResponse(
        String accessToken,
        int expiresIn,
        String username
) {
}
