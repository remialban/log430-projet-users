package ca.log430.users.domain.service;

import ca.log430.users.domain.model.User;
import ca.log430.users.ports.out.UserRepositoryOut;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepositoryOut userRepository;

    public UserService(UserRepositoryOut userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "user", key = "#id")
    public User findById(Integer id) {
        System.out.println("Fetching user with id " + id + " from database.");
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "user", key = "#email", unless = "#result == null")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @CachePut(value="user", key="#result.id")
    public User save(User user) {
        return userRepository.save(user);
    }

}
