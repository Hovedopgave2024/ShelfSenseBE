package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController
{
    @Autowired
    UserRepository userRepository;

    @PostMapping("/createUser")
    public User createUser(@RequestBody User user)
    {
        return userRepository.save(user);
    }


    @PostMapping("/loginUser")
    public ResponseEntity<String> login(@RequestParam String name, @RequestParam String password) {

        Optional<User> userOptional = userRepository.findByName(name);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
