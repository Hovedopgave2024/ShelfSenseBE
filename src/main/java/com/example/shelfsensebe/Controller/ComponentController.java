package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.ComponentSupplierDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Repository.ApiUpdateRepository;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Service.ApiUpdateService;
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

    @Autowired
    ApiUpdateService apiUpdateService;

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
    public ResponseEntity<List<ComponentSupplierDTO>> fetchApiAndUpdateUserComponentsData(@RequestBody Map<String, String> requestBody, HttpSession session) {
        int userId = Integer.parseInt(requestBody.get("userId"));
        String apiKey = requestBody.get("apiKey");
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null || userDTO.getId() != userId) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<ComponentSupplierDTO> updatedComponents = componentService.fetchAndUpdateComponentsWithSupplierInfo(apiKey, userDTO.getId());
            System.out.println(updatedComponents);
            apiUpdateService.updateApiLastUpdated(userDTO.getId());
            return ResponseEntity.ok(updatedComponents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
