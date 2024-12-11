package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.Service.ProductComponentService;
import com.example.shelfsensebe.utility.NumberValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductComponentController
{
    @Autowired
    ProductComponentRepository productComponentRepository;

    @Autowired
    ProductComponentService productComponentService;

    @GetMapping("/productComponents/{productId}")
    public ResponseEntity<List<ProductComponent>> getProductComponents(@PathVariable int productId,  HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(productComponentRepository.findByProduct_id(productId));
    }

    @PostMapping("/productComponents")
    public ResponseEntity<List<ProductComponent>> addProductComponents(@Valid @RequestBody List<ProductComponent> productComponents, HttpSession session) {

        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ProductComponent> savedProductComponents = productComponentService.saveProductComponents(productComponents);

        return ResponseEntity.ok(savedProductComponents);
    }


}
