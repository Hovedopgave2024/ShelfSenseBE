package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService
{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    public void validateOwnership(UserDTO userDTO, Product product) {
        if (userDTO == null || product == null || product.getUser().getId() != userDTO.getId()) {
            throw new IllegalArgumentException("Unauthorized access: You do not own this product.");
        }
    }

    public void deleteProduct(int id, UserDTO userDTO) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Component not found")
        );
        validateOwnership(userDTO, product);
        productRepository.delete(product);
    }
}
