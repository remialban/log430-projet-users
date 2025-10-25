package ca.log430.users.adapter.in;

import ca.log430.users.Response;
import ca.log430.users.domain.model.TokenResponse;
import ca.log430.users.domain.model.User;
import ca.log430.users.domain.model.UserCredential;
import ca.log430.users.ports.out.UserRepositoryOut;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/users/auth")
public class AuthentificationController {

    @Autowired
    UserRepositoryOut userRepository;

    @PostMapping
    public ResponseEntity<Response<TokenResponse>> authenticate(@RequestBody UserCredential user) {

        try {

            Optional<User> userEntity = this.userRepository.findByEmail(user.email);
            if (!userEntity.isPresent()) {
                return ResponseEntity.status(401).body(new Response<>(null, "Invalid email"));
            }

            User userFound =  userEntity.get();
            if (!userFound.getHashedPassword().equals(user.password)) {
                return ResponseEntity.status(401).body(new Response<>(null, "Invalid password"));
            }
            Algorithm algorith = Algorithm.HMAC512("mySecretKey");

            HashMap<String, String> payload = new HashMap<>();

            payload.put("email", user.email);
            payload.put("role", "USER");
            payload.put("expiresAt", String.valueOf(System.currentTimeMillis() + 3600_000));

            String token = JWT.create()
                    .withPayload(payload)
                    .sign(algorith);

            return ResponseEntity.ok(new Response<TokenResponse>(new TokenResponse(token, payload.get("expiresAt")), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new Response<>(null, ex.getMessage()));
        }


    }

    @GetMapping("/check")
    public ResponseEntity<Response<User>> check(@RequestParam String token) {


        Algorithm algorith = Algorithm.HMAC512("mySecretKey");
        JWTVerifier jwtVerifier = JWT.require(algorith).build();

        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String userId = decodedJWT.getClaim("email").asString();
            String role = decodedJWT.getClaim("role").asString();
            String expiresAt = decodedJWT.getClaim("expiresAt").asString();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAtDate = LocalDateTime.ofEpochSecond(Long.parseLong(expiresAt) / 1000, 0, java.time.ZoneOffset.UTC);

            if (now.isAfter(expiresAtDate)) {
                return ResponseEntity.status(401).body(new Response<>(null, "Token expired"));
            }

            User user = this.userRepository.findByEmail(userId).get();
            if (user == null) {
                return ResponseEntity.status(401).body(new Response<>(null, "User not found"));
            }

            return ResponseEntity.ok(new Response<User>(user, null));

        } catch (JWTVerificationException exception) {
            return ResponseEntity.status(401).body(new Response<>(null, "Invalid token"));
        }

        // get payload json
    }

}
