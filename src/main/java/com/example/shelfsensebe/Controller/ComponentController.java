package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import com.example.shelfsensebe.DTO.UserDTO;

import java.util.List;
import java.util.Map;

@RestController
public class ComponentController {

    @Autowired
    ComponentRepository componentRepository;

    @Autowired
    ComponentService componentService;

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
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Component savedComponent = componentService.createComponent(component, userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComponent);
    }

    @PostMapping("components/mouser")
    public ResponseEntity<Map<String, String>> fetchAndUpdateComponentData() {
        try {
            componentService.realFunction();
            Map<String, String> response = Map.of(
                    "status", "success",
                    "message", "Data fetched and updated successfully"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of(
                    "status", "error",
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of(
                    "status", "error",
                    "message", "An unexpected error occurred"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
