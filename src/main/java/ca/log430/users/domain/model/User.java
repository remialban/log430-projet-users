package ca.log430.users.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ReadOnlyProperty
    private Integer id;


    @Column(unique = true, nullable = false)
    private String email;

    private String hashedPassword;

    private String name;
    private String address;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthDate;

    private BigDecimal balance;

    @Version
    private Long version;

    @ReadOnlyProperty
    private UserStatus status = UserStatus.PENDING;

    public User(String email, String hashedPassword, String name, String address, LocalDate birthDate) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.address = address;
        this.birthDate = birthDate;
        this.balance = BigDecimal.ZERO;
    }

    public User() {

    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
