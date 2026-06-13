package authserver.application.dto;

import authserver.domain.model.RoleEntity;
import authserver.domain.model.UserEntity;

import java.util.List;

public record RegisterResponse(
        Long userId,
        String username,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {

    public static RegisterResponse of(UserEntity u){
        return new RegisterResponse(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRoles().stream().map(RoleEntity::getName).toList()
        );
    }
}
