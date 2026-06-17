package authservice.auth.presentation;

import authservice.auth.application.*;
import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Login request for user: {}", request.username());
        Result<AuthResponse> result = authService.login(request);

        return switch (result) {
            case Result.Success<AuthResponse> s ->
                    ResponseEntity.ok(s.value());

            case Result.Error e ->
                    ResponseEntity.status(e.httpStatus())
                            .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @GetMapping("/auth/validate")
    public ResponseEntity<?> validate(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jwt.getClaims());
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Create user request: {}", request.username());
        Result<UserResponse> result = authService.createUser(request);

        return switch (result) {
            case Result.Success<UserResponse> s ->
                    ResponseEntity.status(201).body(s.value());

            case Result.Error e ->
                    ResponseEntity.status(e.httpStatus())
                            .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable String id,
            HttpServletRequest httpRequest
    ) {
        Result<UserResponse> result = authService.findById(id);

        return switch (result) {
            case Result.Success<UserResponse> s ->
                    ResponseEntity.ok(s.value());

            case Result.Error e ->
                    ResponseEntity.status(e.httpStatus())
                            .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @PathVariable String id,
            HttpServletRequest httpRequest
    ) {
        Result<Void> result = authService.deleteUser(id);

        return switch (result) {
            case Result.Success<Void> s ->
                    ResponseEntity.noContent().build();

            case Result.Error e ->
                    ResponseEntity.status(e.httpStatus())
                            .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }
}
