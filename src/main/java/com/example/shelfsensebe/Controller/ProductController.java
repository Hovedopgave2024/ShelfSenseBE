package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Service.ProductService;
import com.example.shelfsensebe.utility.TextSanitizer;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    TextSanitizer textSanitizer;

    @DeleteMapping("/products")
    public ResponseEntity<Integer> deleteProductById(@RequestBody Product product, HttpSession session) {
        int id = product.getId();
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
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product, HttpSession session) throws BadRequestException {
        // Fetch the logged-in user
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = new User();
        user.setId(userDTO.getId());

        if (productRepository.existsByNameAndUser_Id(product.getName(), user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        product.setUser(user);
        String sanitizedName = textSanitizer.sanitize(product.getName());
        product.setName(sanitizedName);

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products")
    public ResponseEntity<Product> updateProductById(@Valid @RequestBody Product updatedProduct, HttpSession session) {
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Product savedProduct = productService.updateProduct(updatedProduct);
            return ResponseEntity.ok(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
