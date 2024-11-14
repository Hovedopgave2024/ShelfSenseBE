package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
public class ComponentController {

    @Autowired
    ComponentRepository componentRepository;
    @Autowired
    private ComponentService componentService;

    @CrossOrigin
    @GetMapping("/components")
    public ResponseEntity<List<Component>> getComponents(HttpSession session) {
        String userName = (String) session.getAttribute("user");
        if (userName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(componentRepository.findByName(userName));
    }

}
