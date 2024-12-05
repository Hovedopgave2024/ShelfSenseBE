package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.ProductComponent;
import com.example.shelfsensebe.Repository.ProductComponentRepository;
import com.example.shelfsensebe.utility.NumberValidator;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductComponentService
{
    @Autowired
    private ProductComponentRepository productComponentRepository;

    @Autowired
    private NumberValidator numberValidator;

    public List<ProductComponent> saveProductComponents(List<ProductComponent> productComponents) {
        productComponents.forEach(productComponent -> {
            try {
                productComponent.setQuantity(numberValidator.validateInt(productComponent.getQuantity(), 0, null));
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        });
        return productComponentRepository.saveAll(productComponents);
    }
}
