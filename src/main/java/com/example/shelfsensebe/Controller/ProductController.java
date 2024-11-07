package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getProducts() {
        return productRepository.findAll();
    }


}
