package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.MouserApiDTO.MouserRequestDTO;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Service.ApiUpdateService;
import com.example.shelfsensebe.Service.ComponentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        public ResponseEntity<Component> createComponent(@Valid @RequestBody Component component, HttpSession session) {
            UserDTO userDTO = (UserDTO) session.getAttribute("user");
            if (userDTO == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Component savedComponent = componentService.createComponent(component, userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComponent);
        }

        @PutMapping("/components/{id}")
        public ResponseEntity<Component> updateComponent(@Valid @PathVariable int id, @RequestBody Component updatedComponent, HttpSession session) {
            UserDTO userDTO = (UserDTO) session.getAttribute("user");
            if (userDTO == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Component savedComponent = componentService.updateComponent(id, updatedComponent, userDTO);
            return ResponseEntity.ok(savedComponent);
        }

        @DeleteMapping("/components/{id}")
        public ResponseEntity<Void> deleteComponent(@PathVariable int id, HttpSession session) {
            UserDTO userDTO = (UserDTO) session.getAttribute("user");
            if (userDTO == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            componentService.deleteComponent(id, userDTO);
            return ResponseEntity.noContent().build();
    }

    @PostMapping("components/mouser")
    public ResponseEntity<List<Component>> fetchApiAndUpdateUserComponentsData(@Valid @RequestBody MouserRequestDTO mouserRequestDTO, HttpSession session) {
        int userId = mouserRequestDTO.getUserId();
        String apiKey = mouserRequestDTO.getApiKey();
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null || userDTO.getId() != userId) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
            List<Component> updatedComponents = componentService.fetchAndUpdateComponentsWithSupplierInfo(apiKey, userDTO.getId());
            apiUpdateService.updateApiLastUpdated(userDTO.getId());
            return ResponseEntity.ok(updatedComponents);
    }

}
