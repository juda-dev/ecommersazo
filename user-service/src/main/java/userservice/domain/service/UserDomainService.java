package userservice.domain.service;

import dev.juda.error.ConflictException;
import dev.juda.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.domain.model.User;
import userservice.domain.repository.UserRepository;

@Service
@Transactional
public class UserDomainService {
    private final UserRepository repo;

    public UserDomainService(UserRepository repo) {
        this.repo = repo;
    }

    public User create(User u){
        if (repo.existsByUsername(u.getUsername())) throw new ConflictException("Username exists: " + u.getUsername());
        if (repo.existsByEmail(u.getEmail())) throw new ConflictException("Email exists: " + u.getEmail());

        return repo.save(u);
    }

    @Transactional(readOnly = true)
    public User findById(Long id){
        return repo.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username){
        return repo.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    public User update(User existing, User updated){
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());

        return repo.save(existing);
    }

    public void delete(Long id){
        repo.delete(findById(id));
    }
}
