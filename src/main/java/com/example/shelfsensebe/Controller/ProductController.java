package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = userDTO.getId();
        return ResponseEntity.ok(productRepository.findByUser_Id(userId));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpSession session) {
        // Fetch the logged-in user
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Create a new User instance
        User user = new User();

        // Set the ID from userDTO
        user.setId(userDTO.getId());
        
        // Validate if the product name already exists for this user
        if (productRepository.existsByNameAndUser_Id(product.getName(), user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Create and save the new Product
        product.setUser(user);
        product.setName(product.getName());
        product.setPrice(product.getPrice());

        productRepository.save(product);

        // Return the created Product
        return ResponseEntity.ok(product);
    }

}
