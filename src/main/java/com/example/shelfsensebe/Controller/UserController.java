package com.example.shelfsensebe.Controller;
import com.example.shelfsensebe.DTO.UpdateUserDTO;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.utility.PasswordValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.shelfsensebe.DTO.UserDTO;

import java.util.Optional;

@RestController
public class UserController
{
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordValidator passwordValidator;

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

        if (userOptional.isEmpty() || !passwordEncoder.matches(user.getPassword(), userOptional.get().getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO userDTO = new UserDTO(userOptional.get().getId(), userOptional.get().getName());
        session.setAttribute("user", userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO, HttpSession session) {
        // Validate session (ensure user is authenticated)
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null || userDTO.getId() != updateUserDTO.getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findById(userDTO.getId());

        // Validate new password (minimum requirements)
        if (
                !passwordValidator.isValidPassword(updateUserDTO.getNewPassword()) ||
                        !passwordEncoder.matches(updateUserDTO.getOldPassword(), user.getPassword())
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.setName(updateUserDTO.getName());
        user.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
        userRepository.save(user);

        UserDTO updatedUser = new UserDTO(user.getId(), updateUserDTO.getName());

        return ResponseEntity.ok(updatedUser);
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
