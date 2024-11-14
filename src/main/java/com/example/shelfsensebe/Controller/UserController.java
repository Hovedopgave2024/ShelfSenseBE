package com.example.shelfsensebe.Controller;
import com.example.shelfsensebe.Model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new IllegalArgumentException("User with name: " + user.getName() + " already exists");
        }
        return userRepository.save(user);
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {

        Optional<User> userOptional = userRepository.findByName(user.getName());

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(user.getPassword())) {
            session.setAttribute("user", userOptional.get().getName());
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @CrossOrigin
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @CrossOrigin
    @GetMapping("/session-status")
    public ResponseEntity<String> sessionStatus(HttpSession session) {
        String userName = (String) session.getAttribute("user");
        if (userName != null) {
            return ResponseEntity.ok("User " + userName + " is logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
    }
}
