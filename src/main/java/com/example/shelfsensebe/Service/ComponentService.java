package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.ComponentSupplierDTO;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public void fetchAndUpdateComponentData() {
        List<ComponentSupplierDTO> components = componentRepository.findBySupplier("Mouser");
        System.out.println(components);
        Map<String, Map<String, Object>> aggregatedResponses = fetchAllComponentData(components);
        System.out.println(aggregatedResponses);
        updateComponentsWithFetchedData(components, aggregatedResponses);
    }

    public Map<String, Map<String, Object>> fetchAllComponentData(List<ComponentSupplierDTO> components) {
        Map<String, Map<String, Object>> aggregatedResponses = new HashMap<>();
        components.forEach(component -> {
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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            Map<String, Object> cleanedResponse = extractRequiredFields(apiResponse);

            if (cleanedResponse != null) {
                aggregatedResponses.put(component.getManufacturerPart(), cleanedResponse);
            }
        });

        return aggregatedResponses;
    }

    private Map<String, Object> extractRequiredFields(Map<String, Object> apiResponse) {
        Map<String, Object> cleanedData = new HashMap<>();

        Map<String, Object> searchResults = (Map<String, Object>) apiResponse.get("SearchResults");
        if (searchResults != null) {
            List<Map<String, Object>> parts = (List<Map<String, Object>>) searchResults.get("Parts");
            if (parts != null && !parts.isEmpty()) {
                Map<String, Object> part = parts.get(0);

                Object inStock = part.get("AvailabilityInStock");
                if (inStock != null) {
                    cleanedData.put("AvailabilityInStock", Integer.parseInt(inStock.toString()));
                }

                List<Map<String, Object>> availabilityOnOrder = (List<Map<String, Object>>) part.get("AvailabilityOnOrder");
                if (availabilityOnOrder != null && !availabilityOnOrder.isEmpty()) {
                    Map<String, Object> firstOrder = availabilityOnOrder.get(0);

                    Object quantity = firstOrder.get("Quantity");
                    if (quantity != null) {
                        cleanedData.put("AvailabilityOnOrderQuantity", Integer.parseInt(quantity.toString()));
                    }

                    Object date = firstOrder.get("Date");
                    if (date != null) {
                        cleanedData.put("AvailabilityOnOrderDate", date.toString().split("T")[0]);
                    }
                }
            }
        }

        return cleanedData.isEmpty() ? null : cleanedData;
    }

    private void updateComponentsWithFetchedData(List<ComponentSupplierDTO> components, Map<String, Map<String, Object>> aggregatedResponses) {
        components.forEach(component -> {
            Map<String, Object> cleanedData = aggregatedResponses.get(component.getManufacturerPart());

            Component entity = componentRepository.findById(component.getId())
                    .orElseThrow(() -> new RuntimeException("Component not found"));

            if (cleanedData != null) {
                Integer supplierStock = (Integer) cleanedData.get("AvailabilityInStock");
                Integer supplierIncomingStock = (Integer) cleanedData.get("AvailabilityOnOrderQuantity");
                String supplierIncomingDate = (String) cleanedData.get("AvailabilityOnOrderDate");

                entity.setSupplierStock(supplierStock);
                entity.setSupplierIncomingStock(supplierIncomingStock);
                entity.setSupplierIncomingDate(supplierIncomingDate != null ? Date.valueOf(supplierIncomingDate) : null);
            } else {
                entity.setSupplierStock(null);
                entity.setSupplierIncomingStock(null);
                entity.setSupplierIncomingDate(null);
            }

            componentRepository.save(entity);
        });
    }
}
