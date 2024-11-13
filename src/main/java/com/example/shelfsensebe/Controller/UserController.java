package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController
{
    @Autowired
    UserRespository userRespository;

    @PostMapping("/createUser")
    public User createUser(@RequestParam String name, @RequestParam String password)
    {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        return userRespository.save(user);
    }


    @PostMapping("/loginUser")
    public ResponseEntity<String> login(@RequestParam String name, @RequestParam String password) {

        Optional<User> userOptional = userRespository.findByUsername(name);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
