package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductComponentService
{
    @Autowired
    private ProductComponentRepository productComponentRepository;

    public List<ProductComponent> saveProductComponents(List<ProductComponent> productComponents) {
        productComponents.forEach(productComponent -> {
            productComponent.setQuantity(productComponent.getQuantity());
        });
        return productComponentRepository.saveAll(productComponents);
    }
}
