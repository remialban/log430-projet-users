package ca.log430.users.ports.out;

import ca.log430.users.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryOut extends JpaRepository<User, Integer> {

    public List<User> findAll();

    public User save(User user);

    Optional<User> findByEmail(String email);

}
