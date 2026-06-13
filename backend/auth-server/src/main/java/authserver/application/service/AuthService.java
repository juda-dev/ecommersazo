package authserver.application.service;

import authserver.application.dto.LoginRequest;
import authserver.application.dto.LoginResponse;
import authserver.application.dto.RegisterRequest;
import authserver.application.dto.RegisterResponse;
import authserver.domain.model.RoleEntity;
import authserver.domain.model.UserEntity;
import authserver.domain.repository.RoleRepository;
import authserver.domain.repository.UserRepository;
import dev.juda.error.ConflictException;
import dev.juda.error.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse login(LoginRequest req){
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject(auth.getName())
                .claim("roles", auth.getAuthorities().stream()
                        .map(Object::toString)
                        .toList())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(token, 3600, auth.getName());
    }

    public RegisterResponse register(RegisterRequest req){
        if (userRepository.existsByUsername(req.username())) throw new ConflictException("There is already a user with a username: " + req.username());

        if (userRepository.existsByEmail(req.email())) throw new ConflictException("There is already a user with a email: " + req.email());

        RoleEntity userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new NotFoundException("ROLE_USER not found in database"));

        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setEnabled(true);
        user.setRoles(Set.of(userRole));

        return RegisterResponse.of(userRepository.save(user));
    }
}
