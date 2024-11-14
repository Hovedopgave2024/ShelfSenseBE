package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @CrossOrigin
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(HttpSession session) {
        String userName = (String) session.getAttribute("user");
        if (userName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(productRepository.findByName(userName));
    }
}
