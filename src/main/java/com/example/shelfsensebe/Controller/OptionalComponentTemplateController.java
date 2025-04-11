package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.OptionalComponentTemplate;
import com.example.shelfsensebe.Service.OptionalComponentTemplateService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OptionalComponentTemplateController {

    @Autowired
    OptionalComponentTemplateService optionalComponentTemplateService;

    @PostMapping("/optionalComponentTemplates")
    public ResponseEntity<OptionalComponentTemplate> createOptionalComponentTemplate(@Valid @RequestBody OptionalComponentTemplate optionalComponentTemplate, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OptionalComponentTemplate savedOptionalComponentTemplate = optionalComponentTemplateService.createOptionalComponentTemplate(optionalComponentTemplate, userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOptionalComponentTemplate);
    }

    @PutMapping("/optionalComponentTemplates/{id}")
    public ResponseEntity<OptionalComponentTemplate> updateOptionalComponentTemplate(@PathVariable int id, @Valid @RequestBody OptionalComponentTemplate updatedOptionalComponentTemplate, HttpSession session) throws BadRequestException {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OptionalComponentTemplate savedOptionalComponentTemplate = optionalComponentTemplateService.updateOptionalComponentTemplate(id, updatedOptionalComponentTemplate);
        return ResponseEntity.ok(savedOptionalComponentTemplate);
    }

    @DeleteMapping("/optionalComponentTemplates/{id}")
    public ResponseEntity<Void> deleteOptionalComponentTemplate(@Valid @PathVariable int id, HttpSession session) throws BadRequestException {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        optionalComponentTemplateService.deleteOptionalComponentTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
