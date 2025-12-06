package ca.log430.users.adapter.in;

import ca.log430.users.Response;
import ca.log430.users.domain.model.User;
import ca.log430.users.domain.model.UserStatus;
import ca.log430.users.domain.service.UserService;
import ca.log430.users.ports.in.UserControllerIn;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController implements UserControllerIn {
    UserService userService;
    KafkaTemplate<String, Object> kafkaTemplate;


    public UserController(UserService userService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
    }
    @PostMapping
    public ResponseEntity<Response<User>> create(@RequestBody User user) {
        if (this.userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Email already exists"));
        }

        try {
            this.userService.save(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, ex.getMessage()));
        }

        Algorithm algorith = Algorithm.HMAC512("mySecretKey");

        HashMap<String, String> payloadToken = new HashMap<>();

        payloadToken.put("email", user.getEmail());
        payloadToken.put("role", "USER");
        payloadToken.put("userId", String.valueOf(user.getId()));
        payloadToken.put("expiresAt", String.valueOf(System.currentTimeMillis() + 3600_000));
        payloadToken.put("operation", "validate");

        String token = JWT.create()
                .withPayload(payloadToken)
                .sign(algorith);





        Map payload = Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "token", token
        );


        kafkaTemplate.send("newUser", payload);

        return new ResponseEntity<>(new Response<>(user, null), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Response<User>> get(@PathVariable Integer id) {
        User user = this.userService.findById(id);

        if (user == null) {

            return new ResponseEntity<>(new Response<User>(null, "User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Response<User>(user, null), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<User>> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(null, "User not found"));
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "address":
                    user.setAddress((String) value);
                    break;
                case "birthDate":
                    user.setBirthDate(LocalDate.parse((String) value));
                    break;
            }
        });

        userService.save(user);
        return ResponseEntity.ok(new Response<>(user, null));
    }

    @GetMapping("/validate")
    public ResponseEntity<Response<User>> check(@RequestParam String token) {
        Algorithm algorith = Algorithm.HMAC512("mySecretKey");
        JWTVerifier jwtVerifier = JWT.require(algorith).build();

        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String expiresAt = decodedJWT.getClaim("expiresAt").asString();
            String userId = decodedJWT.getClaim("email").asString();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAtDate = LocalDateTime.ofEpochSecond(Long.parseLong(expiresAt) / 1000, 0, java.time.ZoneOffset.UTC);


            if (now.isAfter(expiresAtDate)) {
                return ResponseEntity.status(401).body(new Response<>(null, "Token expired"));
            }

            if (decodedJWT.getClaim("operation").asString() == null || !decodedJWT.getClaim("operation").asString().equals("validate")) {
                return ResponseEntity.status(401).body(new Response<>(null, "Invalid token operation"));
            }



            User user = this.userService.findByEmail(userId);
            System.out.println("User found: " + user);
            System.out.println("UserId from token: " + userId);
            if (user == null) {
                return ResponseEntity.status(401).body(new Response<>(null, "User not found"));
            }

            user.setStatus(UserStatus.ACTIVE);

            this.userService.save(user);

            return ResponseEntity.ok(new Response<User>(user, null));

        } catch (JWTVerificationException exception) {
            return ResponseEntity.status(401).body(new Response<>(null, "Invalid token:" + exception.getMessage()));
        }

    }
}
