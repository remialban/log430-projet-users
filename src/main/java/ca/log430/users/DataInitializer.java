package ca.log430.users;

import ca.log430.users.domain.model.User;
import ca.log430.users.domain.model.UserStatus;
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
            User user = new User("alice@example.com", "passwordAlice", "Alice", "Street 1", LocalDate.of(2000, 1, 1);
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);

            User user2 = new User("bob@example.com", "passwordBob", "Boob", "Street 2", LocalDate.of(2000, 2, 1);
            user2.setStatus(UserStatus.ACTIVE);
            userRepository.save(user2);

            System.out.println("✅ Données initiales insérées !");
        }
    }

}
