package ca.log430.users;

import ca.log430.users.domain.model.User;
import ca.log430.users.ports.out.UserRepositoryOut;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepositoryOut userRepository;

    public DataInitializer(UserRepositoryOut userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("alice@example.com", "passwordAlice", "Alice", "Street 1", LocalDate.of(2000, 1, 1)));
            userRepository.save(new User("bob@example.com", "passwordBob", "Boob", "Street 2", LocalDate.of(2000, 2, 1)));
            System.out.println("✅ Données initiales insérées !");
        }
    }

}
