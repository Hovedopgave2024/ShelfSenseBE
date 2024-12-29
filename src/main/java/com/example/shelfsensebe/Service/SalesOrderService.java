package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.*;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.Repository.ProductRepository;
import com.example.shelfsensebe.Repository.SalesOrderRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import com.example.shelfsensebe.utility.StatusCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesOrderService {

    @Autowired
    SalesOrderRepository salesOrderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    TextSanitizer textSanitizer;
    @Autowired
    ComponentRepository componentRepository;
    @Autowired
    StatusCalculator statusCalculator;

    @Transactional
    public SalesOrder createSalesOrder(SalesOrder salesOrder, UserDTO userDTO) {
        // Set the user to the sales order
        User user = new User();
        user.setId(userDTO.getId());
        salesOrder.setUser(user);

        // Set the other properties
        salesOrder.setCreatedDate(salesOrder.getCreatedDate());
        salesOrder.setPrice(salesOrder.getPrice());
        salesOrder.setQuantity(salesOrder.getQuantity());
        salesOrder.setProductId(salesOrder.getProductId());
        salesOrder.setProductName(textSanitizer.sanitize(salesOrder.getProductName()));

        // Fetch the product
        Product product = productRepository.findById(salesOrder.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
        );

        // Fetch the components associated with the product
        List<ProductComponent> productComponents = product.getProductComponentList();
        if (productComponents == null || productComponents.isEmpty()) {
            return salesOrderRepository.save(salesOrder);
        }

        List<Component> updatedComponents = new ArrayList<>();

        // Update stock for each component
        for (ProductComponent productComponent : productComponents) {
            int componentId = productComponent.getComponentId();
            int totalQuantityUsed = productComponent.getQuantity() * salesOrder.getQuantity();

            Component component = componentRepository.findById(componentId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component not found with ID: " + componentId)
            );

            // Check stock availability
            if (component.getStock() < totalQuantityUsed) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for component ID: " + componentId);
            }

            component.setStock(component.getStock() - totalQuantityUsed);
            component.setStockStatus(statusCalculator.calculateStatus(
                    component.getStock(),
                    component.getSafetyStock(),
                    component.getSafetyStockRop()
            ));

            updatedComponents.add(component);

        }

        componentRepository.saveAll(updatedComponents);

        // Save the sales order
        return salesOrderRepository.save(salesOrder);
    }

    public SalesOrder updateSalesOrder(SalesOrder salesOrder) {
        SalesOrder existingSalesOrder = salesOrderRepository.findById(salesOrder.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales order not found")
        );

        // User is not updated, stays the same.
        // Quantity is not updated - stays the same.
        existingSalesOrder.setPrice(salesOrder.getPrice());
        // productId is not updated - stays the same.
        // productName is not updated - stays the same.
        existingSalesOrder.setCreatedDate(salesOrder.getCreatedDate());

        productRepository.findById(salesOrder.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
        );

        return salesOrderRepository.save(existingSalesOrder);
    }

    @Transactional
    public void deleteSalesOrder(int salesOrderId) {
        // Fetch the SalesOrder
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sales order not found")
        );

        // Fetch the Product associated with the SalesOrder
        Product product = productRepository.findById(salesOrder.getProductId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found")
        );

        // Fetch the components associated with the product
        List<ProductComponent> productComponents = product.getProductComponentList();
        if (productComponents == null || productComponents.isEmpty()) {
            salesOrderRepository.delete(salesOrder);
            return;
        }

        List<Component> updatedComponents = new ArrayList<>();

        // Revert stock for each component
        for (ProductComponent productComponent : productComponents) {
            int componentId = productComponent.getComponentId();
            int revertQuantity = productComponent.getQuantity() * salesOrder.getQuantity();

            Component component = componentRepository.findById(componentId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component not found with ID: " + componentId)
            );

            // Revert the stock
            component.setStock(component.getStock() + revertQuantity);
            component.setStockStatus(statusCalculator.calculateStatus(
                    component.getStock(),
                    component.getSafetyStock(),
                    component.getSafetyStockRop()
            ));

            updatedComponents.add(component);
        }

        componentRepository.saveAll(updatedComponents);
        // Delete the SalesOrder
        salesOrderRepository.delete(salesOrder);
    }
}
