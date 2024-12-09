package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.StatusCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ComponentService
{
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private WebClient webClient;
    @Autowired
    private StatusCalculator statusCalculator;

    // Dedicated method for validating ownershipp
    public void validateOwnership(UserDTO userDTO, Component component) {
        if (userDTO == null || component == null || component.getUser().getId() != userDTO.getId()) {
            throw new IllegalArgumentException("Unauthorized access: You do not own this component.");
        }
    }

    public Component createComponent(Component component, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());

        component.setStockStatus(statusCalculator.calculateStatus(
                component.getStock(),
                component.getSafetyStock(),
                component.getSafetyStockRop()
        ));

        component.setUser(user);
        return componentRepository.save(component);
    }

    public Component updateComponent(int id, Component updatedComponent, UserDTO userDTO)
    {
        Component existingComponent = componentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Component Not Found")
        );
        validateOwnership(userDTO, existingComponent);

        if (updatedComponent.getStock() > existingComponent.getStock()) {
            int newStock = updatedComponent.getStock() - existingComponent.getStock();
            existingComponent.setStock(existingComponent.getStock() + newStock);
        } else {
            existingComponent.setStock(updatedComponent.getStock());
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

        existingComponent.setStockStatus(statusCalculator.calculateStatus(updatedComponent.getStock(),
                updatedComponent.getSafetyStock(), updatedComponent.getSafetyStockRop()));

     if (updatedComponent.getSupplierSafetyStock() != null && updatedComponent.getSupplierSafetyStockRop() != null && existingComponent.getSupplierStock() != null)
     {
            existingComponent.setSupplierStockStatus(statusCalculator.calculateStatus(
                    existingComponent.getSupplierStock(),
                    updatedComponent.getSupplierSafetyStock(),
                    updatedComponent.getSupplierSafetyStockRop()
            ));
     }


        return componentRepository.save(existingComponent);
    }

    public void deleteComponent(int id, UserDTO userDTO) {
        Component component = componentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Component not found")
        );
        validateOwnership(userDTO, component);
        componentRepository.delete(component);
    }

    public List<Component> fetchAndUpdateComponentsWithSupplierInfo(String apiKey, int userId) {
        // Find components with supplier = Mouser and only fetch the rows in ComponentSupplierDTO
        List<Component> components = componentRepository.findBySupplierAndUser_Id("Mouser", userId);
        List<Component> updatedComponents = new ArrayList<>();

        components.forEach(component -> {
            try {
                // mouser API request:
                Map<String, Object> requestBody = Map.of(
                        "SearchByKeywordMfrNameRequest", Map.of(
                                "manufacturerName", component.getManufacturer(),
                                "keyword", component.getManufacturerPart(),
                                "records", 1,
                                "pageNumber", 0,
                                "searchOptions", "",
                                "searchWithYourSignUpLanguage", ""
                        )
                );

                Map<String, Object> apiResponse = webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/search/keywordandmanufacturer")
                                .queryParam("apiKey", apiKey)
                                .build())
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                        .block();
                Map<String, Object> searchResults = (Map<String, Object>) apiResponse.get("SearchResults");

                // Clean the response to handle null errors and find the necessary info.
                if (searchResults != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) searchResults.get("Parts");
                    if (parts != null && !parts.isEmpty()) {
                        Map<String, Object> part = parts.get(0);

                        Object inStock = part.get("AvailabilityInStock");
                        int stockValue = Integer.parseInt(inStock.toString());
                        if (stockValue > 0) {
                            component.setSupplierStock(stockValue);
                        } else component.setSupplierStock(null);

                        List<Map<String, Object>> availabilityOnOrder = (List<Map<String, Object>>) part.get("AvailabilityOnOrder");
                        if (availabilityOnOrder != null && !availabilityOnOrder.isEmpty()) {
                            Map<String, Object> firstOrder = availabilityOnOrder.get(0);
                            Object quantity = firstOrder.get("Quantity");
                            if (quantity != null) {
                                component.setSupplierIncomingStock(Integer.parseInt(quantity.toString()));
                            } else component.setSupplierIncomingStock(null);
                            Object date = firstOrder.get("Date");
                            if (date != null) {
                                component.setSupplierIncomingDate(Date.valueOf(date.toString().split("T")[0]));
                            } else component.setSupplierIncomingDate(null);
                        } else {
                            component.setSupplierIncomingDate(null);
                            component.setSupplierIncomingStock(null);
                        }
                    }
                }

                component.setSupplierStockStatus(statusCalculator.calculateStatus(
                        component.getSupplierStock(),
                        component.getSupplierSafetyStock(),
                        component.getSupplierSafetyStockRop()
                ));

                // save updated component columns and add them to a list that is returned to the frontend.
                updatedComponents.add(component);

                componentRepository.save(component);
            } catch(Exception e) {
                System.err.println("Error processing component: " + component.getId() + " - " + e.getMessage());
            }
        });

        return updatedComponents;
    }

}
