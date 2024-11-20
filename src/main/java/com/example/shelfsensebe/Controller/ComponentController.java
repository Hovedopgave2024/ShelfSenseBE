package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Service.ComponentService;
import com.example.shelfsensebe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import com.example.shelfsensebe.DTO.UserDTO;

import java.util.List;

@RestController
public class ComponentController {

    @Autowired
    ComponentRepository componentRepository;

    @Autowired
    ComponentService componentService;

    @Autowired
    UserService userService;

    @GetMapping("/components")
    public ResponseEntity<List<Component>> getComponents(HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = userDTO.getId();
        return ResponseEntity.ok(componentRepository.findByUser_Id(userId));
    }

    @PostMapping("/components")
    public ResponseEntity<Component> createComponent(@RequestBody Component component, HttpSession session) {
        try {
            // Retrieve the logged-in user from the session
            User user = userService.getCurrentUser(session);
            // Pass the user to the service
            Component savedComponent = componentService.createComponent(component, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComponent);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
