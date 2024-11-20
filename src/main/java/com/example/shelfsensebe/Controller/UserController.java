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

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(HttpSession session, @PathVariable int id) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null || userDTO.getId() != id) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userRepository.findById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody User user, HttpSession session) {

        Optional<User> userOptional = userRepository.findByName(user.getName());

        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDTO userDTO = new UserDTO(userOptional.get().getId(), userOptional.get().getName());
        session.setAttribute("user", userDTO);
        return ResponseEntity.ok(userDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    // Checking if session is active
    @GetMapping("/session")
    public ResponseEntity<UserDTO> sessionStatus(HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userDTO);
    }

}
