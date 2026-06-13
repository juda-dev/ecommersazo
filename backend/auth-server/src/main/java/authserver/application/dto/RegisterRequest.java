package authserver.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 4) String username,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank @Size(min = 2, max = 20) String firstName,
        @NotBlank @Size(min = 2, max = 20) String lastName
) {
}
