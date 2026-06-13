package authserver.infrastructure.web;

import authserver.application.dto.LoginRequest;
import authserver.application.dto.LoginResponse;
import authserver.application.dto.RegisterRequest;
import authserver.application.dto.RegisterResponse;
import authserver.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService svc;

    public AuthController(AuthService svc) {
        this.svc = svc;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req){
        return svc.login(req);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest req){
        return svc.register(req);
    }

}
