package authservice.auth.application;

import authservice.auth.domain.UserEntity;
import authservice.auth.infrastructure.UserRepository;
import dev.juda.error.ApplicationException;
import dev.juda.result.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final RestTemplate restTemplate;
    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public Result<AuthResponse> login(AuthRequest request) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", request.username());
            body.add("password", request.password());
            body.add("grant_type", "password");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> tokenData = response.getBody();
                AuthResponse authResponse = new AuthResponse(
                        (String) tokenData.get("access_token"),
                        (String) tokenData.get("refresh_token"),
                        ((Number) tokenData.get("expires_in")).intValue(),
                        (String) tokenData.get("token_type")
                );
                log.info("User {} logged in successfully", request.username());
                return Result.success(authResponse);
            }

            return Result.error("AUTH_FAILED", "Authentication failed", null);

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.username(), e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                return Result.error("INVALID_CREDENTIALS", "Invalid username or password", null);
            }
            return Result.error("AUTH_ERROR", "Authentication service error: " + e.getMessage(), null);
        }
    }

    @Transactional
    public Result<UserResponse> createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return Result.error("USERNAME_EXISTS", "Username '" + request.username() + "' is already taken", null);
        }
        if (userRepository.existsByEmail(request.email())) {
            return Result.error("EMAIL_EXISTS", "Email '" + request.email() + "' is already in use", null);
        }

        try {
            String keycloakId = keycloakAdminService.createUser(
                    request.username(),
                    request.email(),
                    request.password()
            );

            UserEntity userEntity = new UserEntity(
                    UUID.randomUUID().toString(),
                    request.username(),
                    request.email(),
                    keycloakId
            );
            userEntity = userRepository.save(userEntity);

            log.info("User {} created with local ID {}", request.username(), userEntity.getId());

            UserResponse response = new UserResponse(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getKeycloakId(),
                    userEntity.getCreatedAt()
            );
            return Result.success(response);

        } catch (ApplicationException e) {
            return Result.error(e.getErrorCode(), e.getMessage(), null);
        }
    }

    @SuppressWarnings("unchecked")
    public Result<UserResponse> findById(String userId) {
        return userRepository.findById(userId)
                .map(user -> Result.success(new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getKeycloakId(),
                        user.getCreatedAt()
                )))
                .orElseGet(() ->  (Result<UserResponse>) (Result<?>) Result.error(
                        "USER_NOT_FOUND",
                        "User with ID " + userId + " not found",
                        null));
    }

    @Transactional
    public Result<Void> deleteUser(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return Result.error("USER_NOT_FOUND", "User with ID " + userId + " not found", null);
        }

        try {
            keycloakAdminService.deleteUser(user.getKeycloakId());
        } catch (ApplicationException e) {
            log.error("Failed to delete user from Keycloak: {}", e.getMessage());
            return Result.error("KEYCLOAK_ERROR", "Failed to delete user from Keycloak: " + e.getMessage(), null);
        }

        userRepository.delete(user);
        log.info("User {} deleted", userId);
        return Result.success();
    }
}

