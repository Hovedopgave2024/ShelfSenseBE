package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @DeleteMapping("/products")
    public ResponseEntity<Integer> deleteProductById(@RequestBody Map<String, String> requestBody, HttpSession session) {
        int id = Integer.parseInt(requestBody.get("id"));
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        productService.deleteProduct(id, userDTO);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProductsByUserId(HttpSession session) {
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

    @PutMapping("/products")
    public ResponseEntity<Product> updateProductById(@RequestBody Product updatedProduct) {
        try {
            Product savedProduct = productService.updateProduct(updatedProduct);
            return ResponseEntity.ok(savedProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
