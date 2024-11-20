package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.Service.ProductComponentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductComponentController
{
    @Autowired
    ProductComponentRepository productComponentRepository;

    @Autowired
    ProductComponentService productComponentService;

    @PostMapping("/productComponents")
    public ResponseEntity<List<ProductComponent>> addProductComponents(
            @RequestBody List<ProductComponent> productComponents, HttpSession session) {

        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ProductComponent> savedProductComponents = productComponentService.saveProductComponents(productComponents);

        return ResponseEntity.ok(savedProductComponents);
    }


}
