package ca.log430.users.adapter.in;

import ca.log430.users.Response;
import ca.log430.users.domain.model.User;
import ca.log430.users.ports.in.UserControllerIn;
import ca.log430.users.ports.out.UserRepositoryOut;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController implements UserControllerIn {
    UserRepositoryOut userRepository;

    public UserController(UserRepositoryOut userRepository) {
        this.userRepository = userRepository;
    }
    @PostMapping
    public ResponseEntity<Response<User>> create(@RequestBody User user) {
        if (this.userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Email already exists"));
        }

        try {
            this.userRepository.save(user);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, ex.getMessage()));
        }


        return new ResponseEntity<>(new Response<>(user, null), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Response<User>> get(@PathVariable Integer id) {
        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent()) {

            return new ResponseEntity<>(new Response<User>(null, "User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Response<User>(user.get(), null), HttpStatus.OK);
    }


}
