package authservice.auth.application;

import dev.juda.error.ApplicationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(
            Keycloak keycloak,
            @Value("${keycloak.realm}") String realm
    ) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String createUser(String username, String email, String password) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEmailVerified(true);
            user.setEnabled(true);

            UsersResource usersResource = keycloak.realm(realm).users();
            try (Response response = usersResource.create(user)) {
                if (response.getStatus() != 201) {
                    String errorBody = response.readEntity(String.class);
                    log.error("Keycloak returned status {}: {}", response.getStatus(), errorBody);
                    throw new ApplicationException(
                            "KEYCLOAK_ERROR",
                            "Failed to create user in Keycloak: " + errorBody
                    );
                }

                String location = response.getHeaderString("Location");
                String userId = location.substring(location.lastIndexOf('/') + 1);
                log.info("User created in Keycloak with ID: {}", userId);

                assignRole(userId);

                setPassword(userId, password);

                return userId;
            }
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating user in Keycloak: {}", e.getMessage(), e);
            throw new ApplicationException(
                    "KEYCLOAK_ERROR",
                    "Unexpected error creating user in Keycloak: " + e.getMessage(),
                    e
            );
        }
    }

    public void deleteUser(String userId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            userResource.toRepresentation();
            userResource.remove();
            log.info("User {} deleted from Keycloak", userId);
        } catch (NotFoundException e) {
            throw new ApplicationException(
                    "USER_NOT_FOUND",
                    "User with ID " + userId + " not found in Keycloak"
            );
        } catch (Exception e) {
            log.error("Error deleting user {} from Keycloak: {}", userId, e.getMessage());
            throw new ApplicationException(
                    "KEYCLOAK_ERROR",
                    "Failed to delete user from Keycloak: " + e.getMessage(),
                    e
            );
        }
    }

    public UserRepresentation findByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(realm).users()
                .searchByUsername(username, true);
        return users.isEmpty() ? null : users.getFirst();
    }

    private void assignRole(String userId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            RoleRepresentation role = keycloak.realm(realm).roles()
                    .get("ROLE_USER").toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(role));
            log.info("Role {} assigned to user {}", "ROLE_USER", userId);
        } catch (NotFoundException e) {
            log.warn("Role {} not found in Keycloak — skipping assignment", "ROLE_USER");
        }
    }

    private void setPassword(String userId, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(userId)
                .resetPassword(credential);
        log.info("Password set for user {}", userId);
    }
}

