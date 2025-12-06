package ca.log430.users.domain.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserCreation() {
        User user = new User(
                "john.doe@lorem.fr",
                "hashed_password_123",
                "John Doe",
                "123 Main St, Anytown, USA",
                java.time.LocalDate.of(1990, 1, 1)
        );
        user.setBalance(new BigDecimal("0"));

        assert user.getEmail().equals("john.doe@lorem.fr");
        assert user.getHashedPassword().equals("hashed_password_123");
        assert user.getName().equals("John Doe");
        assert user.getAddress().equals("123 Main St, Anytown, USA");
        assert user.getBirthDate().equals(java.time.LocalDate.of(1990, 1, 1));
        assert user.getBalance().equals(new BigDecimal("0"));

        // check validation :
        var violations = validator.validate(user);
        assertEquals(0, violations.size());

    }

    @Test
    public void testUserCreationWithInvalidBirthDate() {
        User user = new User(
                "john.doe@lorem.fr",
                "hashed_password_123",
                "John Doe",
                "123 Main St, Anytown, USA",
                java.time.LocalDate.of(5000, 1, 1)
        );
        user.setBalance(new BigDecimal("0"));

        // check validation :
        var violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("La date de naissance doit être dans le passé", violations.iterator().next().getMessage());

    }
}
