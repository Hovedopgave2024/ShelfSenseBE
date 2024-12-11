package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Product;
import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService
{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ProductComponentRepository productComponentRepository;

    @Autowired
    private ComponentRepository componentRepository;

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

    @Transactional
    public Product updateProduct(Product updatedProduct) {
        // Fetch the existing product from the database
        Product existingProduct = productRepository.findById(updatedProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (updatedProduct.getName() == null || updatedProduct.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        // update product
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());

        // Get existing and updated productComponent lists
        List<ProductComponent> existingProductComponents = existingProduct.getProductComponentList() != null
                ? existingProduct.getProductComponentList()
                : new ArrayList<>();
        List<ProductComponent> updatedProductComponents = updatedProduct.getProductComponentList() != null
                ? updatedProduct.getProductComponentList()
                : new ArrayList<>();

        // Track productComponent to delete
        List<ProductComponent> productComponentsToUpdate = new ArrayList<>();
        List<ProductComponent> productComponentsToDelete = new ArrayList<>();
        List<ProductComponent> productComponentsToAdd = new ArrayList<>();

        // Process existing components
        for (ProductComponent existingPC : existingProductComponents) {
            boolean found = false;

            for (ProductComponent updatedPC : new ArrayList<>(updatedProductComponents)) {
                if (existingPC.getId() == updatedPC.getId()) {
                    found = true;

                    // Update quantity if it differs
                    if (existingPC.getQuantity() != updatedPC.getQuantity()) {
                        existingPC.setQuantity(updatedPC.getQuantity());
                    }

                    int existingComponentId = existingPC.getComponentId();
                    int updatedComponentId = updatedPC.getComponentId();

                    if (existingComponentId != updatedComponentId) {
                        Component newComponent = componentRepository.findById(updatedPC.getComponentId())
                                .orElseThrow(() -> new IllegalArgumentException("Component not found for ID: " + updatedPC.getComponentId()));
                        existingPC.setComponent(newComponent);
                    }

                    productComponentsToUpdate.add(existingPC);
                    updatedProductComponents.remove(updatedPC);
                    break;
                }
            }

            if (!found) {
                productComponentsToDelete.add(existingPC);
            }
        }

        // Handle deletions
        productComponentRepository.deleteAll(productComponentsToDelete);
        existingProductComponents.removeAll(productComponentsToDelete);

        // Handle additions
        for (ProductComponent newPC : updatedProductComponents) {
            if (newPC.getComponentId() != null) {
                Component component = componentRepository.findById(newPC.getComponentId())
                        .orElseThrow(() -> new IllegalArgumentException("Component not found for ID: " + newPC.getComponentId()));
                newPC.setComponent(component);
            } else {
                throw new IllegalArgumentException("New ProductComponent must have a valid componentId");
            }
            newPC.setProduct(existingProduct);
            productComponentsToAdd.add(newPC);
        }

        // Batch save all updates and additions. Batch size is set in application properties
        productComponentRepository.saveAll(productComponentsToUpdate);
        productComponentRepository.saveAll(productComponentsToAdd);
        existingProductComponents.addAll(productComponentsToAdd);

        // Save the updated product
        return productRepository.save(existingProduct);
    }
}
