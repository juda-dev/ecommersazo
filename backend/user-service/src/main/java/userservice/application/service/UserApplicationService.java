package userservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.application.dto.CreateUserRequest;
import userservice.application.dto.UpdateUserRequest;
import userservice.application.dto.UserResponse;
import userservice.domain.model.User;
import userservice.domain.repository.UserRepository;
import userservice.domain.service.UserDomainService;

@Service
@Transactional
public class UserApplicationService {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;

    public UserApplicationService(UserDomainService userDomainService, UserRepository userRepository) {
        this.userDomainService = userDomainService;
        this.userRepository = userRepository;
    }

    public UserResponse create (CreateUserRequest req){
        User u = new User();
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setEmail(req.email());
        u.setUsername(req.username());
        u.setPhone(req.phone());

        return UserResponse.from(userDomainService.create(u));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id){
        return UserResponse.from(userDomainService.findById(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getByUsername(String username){
        return UserResponse.from(userDomainService.findByUsername(username));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable p){
        return userRepository.findAll(p).map(UserResponse::from);
    }

    public UserResponse update(Long id, UpdateUserRequest req){
        User existing = userDomainService.findById(id);
        User updated = new User();
        updated.setFirstName(req.firstName());
        updated.setLastName(req.lastName());
        updated.setPhone(req.phone());
        updated.setEmail(req.email());

        return UserResponse.from(userDomainService.update(existing, updated));
    }

    public void delete(Long id){
        userDomainService.delete(id);
    }
}
