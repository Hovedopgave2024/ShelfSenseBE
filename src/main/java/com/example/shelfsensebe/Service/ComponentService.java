package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.ComponentSupplierDTO;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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

    @Value("${api.mouser.key}")
    private String apiKey;

    public Component createComponent(Component component, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        component.setUser(user);
        return componentRepository.save(component);
    }


    public List<ComponentSupplierDTO> fetchAndUpdateComponentWithSupplierInfo() {
        // Find components with supplier = Mouser and only fetch the rows in ComponentSupplierDTO
        List<ComponentSupplierDTO> components = componentRepository.findBySupplier("Mouser");
        List<ComponentSupplierDTO> updatedComponents = new ArrayList<>();

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
                            component.setSupplierStock(Integer.parseInt(inStock.toString()));
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

                // save updated component columns and add them to a list that is returned to the frontend.
                updatedComponents.add(component);

                componentRepository.updateComponentData(component.getId(), component.getSupplierStock(), component.getSupplierIncomingStock(), component.getSupplierIncomingDate());
            } catch(Exception e) {
                System.err.println("Error processing component: " + component.getId() + " - " + e.getMessage());
            }
        });

        return updatedComponents;
    }
}
