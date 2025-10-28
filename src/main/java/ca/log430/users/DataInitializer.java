package ca.log430.users;

import ca.log430.users.domain.model.User;
import ca.log430.users.ports.out.UserRepositoryOut;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepositoryOut userRepository;

    public DataInitializer(UserRepositoryOut userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Alice", "alice@example.com"));
            userRepository.save(new User("Bob", "bob@example.com"));
            System.out.println("✅ Données initiales insérées !");
        }
    }

}
