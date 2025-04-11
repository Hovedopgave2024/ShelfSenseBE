package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.*;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SalesOrderService {

    @Autowired
    SalesOrderRepository salesOrderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    TextSanitizer textSanitizer;
    @Autowired
    ComponentRepository componentRepository;

    public SalesOrder createSalesOrder(SalesOrder salesOrder, UserDTO userDTO) {
        // Set the user to the sales order
        User user = new User();
        user.setId(userDTO.getId());
        salesOrder.setUser(user);

        salesOrder.setCreatedDate(salesOrder.getCreatedDate());
        salesOrder.setPrice(salesOrder.getPrice());

        if (salesOrder.getSalesOrderProducts() == null || salesOrder.getSalesOrderProducts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one product is required in the sales order.");
        }

        List<Component> updatedComponents = new ArrayList<>();

        for (SalesOrderProduct salesOrderProduct : salesOrder.getSalesOrderProducts()) {

            // Fetch the product
            Product product = productRepository.findById(salesOrderProduct.getProductId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
            );

            // Associate the SalesOrderProduct with SalesOrder
            salesOrderProduct.setSalesOrder(salesOrder);

            // Fetch the components associated with the product
            List<ProductComponent> productComponents = product.getProductComponentList();
            if (productComponents != null && !productComponents.isEmpty()) {
                // Update stock for each component
                for (ProductComponent productComponent : productComponents) {
                    int componentId = productComponent.getComponentId();
                    int totalQuantityUsed = productComponent.getQuantity() * salesOrderProduct.getQuantity();

                    Component component = componentRepository.findById(componentId).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component not found with ID: " + componentId)
                    );

                    // Check stock availability
                    if (component.getStock() < totalQuantityUsed) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Insufficient stock for component ID: " + componentId);
                    }

                    component.setStock(component.getStock() - totalQuantityUsed);
                    updatedComponents.add(component);
                }
            }
        }

        // Save the updated components
        componentRepository.saveAll(updatedComponents);

        // Save the sales order and its products
        return salesOrderRepository.save(salesOrder);
    }

    public void deleteSalesOrder(int salesOrderId) {
        // Fetch the SalesOrder
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales order not found")
        );

        List<Component> updatedComponents = new ArrayList<>();

        // Loop through all SalesOrderProducts in this order
        for (SalesOrderProduct salesOrderProduct : salesOrder.getSalesOrderProducts()) {
            // Fetch the product associated with this SalesOrderProduct
            Product product = productRepository.findById(salesOrderProduct.getProductId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
            );

            // Fetch the components associated with the product
            List<ProductComponent> productComponents = product.getProductComponentList();
            if (productComponents != null && !productComponents.isEmpty()) {
                for (ProductComponent productComponent : productComponents) {
                    int componentId = productComponent.getComponentId();
                    int revertQuantity = productComponent.getQuantity() * salesOrderProduct.getQuantity();

                    Component component = componentRepository.findById(componentId).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component not found with ID: " + componentId)
                    );

                    // Revert the stock
                    component.setStock(component.getStock() + revertQuantity);
                    updatedComponents.add(component);
                }
            }
        }

        // Save updated stock
        componentRepository.saveAll(updatedComponents);

        // Delete the SalesOrder
        salesOrderRepository.delete(salesOrder);
    }
}
