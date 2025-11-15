package ca.log430.users.adapter.in;

import ca.log430.users.Response;
import ca.log430.users.domain.model.User;
import ca.log430.users.domain.service.UserService;
import ca.log430.users.ports.in.UserControllerIn;
import ca.log430.users.ports.out.UserRepositoryOut;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController implements UserControllerIn {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
