package com.example.shelfsensebe.Controller;
import com.example.shelfsensebe.Model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.shelfsensebe.DTO.UserDTO;

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

        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        UserDTO userDTO = new UserDTO(userOptional.get().getId(), userOptional.get().getName());
        session.setAttribute("user", userDTO);
        return ResponseEntity.ok("Login successful");
    }

    @CrossOrigin
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @CrossOrigin
    @GetMapping("/session")
    public ResponseEntity<String> sessionStatus(HttpSession session) {
        String userId = (String) session.getAttribute("user");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
        return ResponseEntity.ok("User with id: " + userId + " is logged in");
    }
}
