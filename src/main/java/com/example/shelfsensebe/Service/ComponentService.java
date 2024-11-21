package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComponentService
{
    @Autowired
    private ComponentRepository componentRepository;

    public Component createComponent(Component component, UserDTO userDTO) {
        // Map UserDTO to User
        User user = new User();
        user.setId(userDTO.getId());
        component.setUser(user);
        // Save and return the component
        return componentRepository.save(component);
    }

    public Component updateComponent(int id, Component updatedComponent, UserDTO userDTO) {
        // If the component with the given ID does not exist, throw an IllegalArgumentException.
        Component existingComponent = componentRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("Component Not Found")
        );

        // If the user ID associated with the component does not match the logged-in user's ID, throw a SecurityException.
        if (existingComponent.getUserId() != userDTO.getId()) {
            throw new SecurityException("Unauthorized to update this component");
        }

        existingComponent.setName(updatedComponent.getName());
        existingComponent.setType(updatedComponent.getType());
        existingComponent.setFootprint(updatedComponent.getFootprint());
        existingComponent.setManufacturerPart(updatedComponent.getManufacturerPart());
        existingComponent.setPrice(updatedComponent.getPrice());
        existingComponent.setSupplier(updatedComponent.getSupplier());
        existingComponent.setStock(updatedComponent.getStock());
        existingComponent.setSafetyStock(updatedComponent.getSafetyStock());
        existingComponent.setSafetyStockRop(updatedComponent.getSafetyStockRop());
        existingComponent.setSupplierSafetyStock(updatedComponent.getSupplierSafetyStock());
        existingComponent.setSupplierSafetyStockRop(updatedComponent.getSupplierSafetyStockRop());
        existingComponent.setDesignator(updatedComponent.getDesignator());
        existingComponent.setManufacturer(updatedComponent.getManufacturer());
        existingComponent.setSupplierPart(updatedComponent.getSupplierPart());

        return componentRepository.save(existingComponent);
    }
}
