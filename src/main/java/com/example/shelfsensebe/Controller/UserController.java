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

    @CrossOrigin
    @PostMapping("/users")
    public User users(@RequestBody User user)
    {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        return userRepository.save(user);
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {

        Optional<User> userOptional = userRepository.findByName(user.getName());

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(user.getPassword())) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
