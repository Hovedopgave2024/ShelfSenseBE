package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
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
        // Map UserDTO to User
        User user = new User();
        user.setId(userDTO.getId());
        component.setUser(user);
        // Save and return the component
        return componentRepository.save(component);
    }

    public Map<String, Object> fetchAllComponentData() {
        // Fetch all components from the repository
        List<Component> components = componentRepository.findAll();

        // Aggregate cleaned API responses
        Map<String, Object> aggregatedResponses = new HashMap<>();

        // Fetch data for each component
        components.forEach(component -> {
            // Build the POST request body
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

            // Make the API call
            Map<String, Object> apiResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/search/keywordandmanufacturer")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(); // Blocking call for simplicity

            // Extract only the required fields
            Map<String, Object> cleanedResponse = extractRequiredFields(apiResponse);

            // Save the cleaned response
            if (cleanedResponse != null) {
                aggregatedResponses.put(component.getManufacturerPart(), cleanedResponse);
            }
        });

        return aggregatedResponses;
    }

    /**
     * Extracts only "AvailabilityInStock" and "AvailabilityOnOrder" from the API response.
     */
    private Map<String, Object> extractRequiredFields(Map<String, Object> apiResponse) {
        Map<String, Object> cleanedData = new HashMap<>();

        // Navigate the API response structure
        Map<String, Object> searchResults = (Map<String, Object>) apiResponse.get("SearchResults");
        if (searchResults != null) {
            List<Map<String, Object>> parts = (List<Map<String, Object>>) searchResults.get("Parts");
            if (parts != null && !parts.isEmpty()) {
                Map<String, Object> part = parts.get(0); // Assuming only one part is returned

                // Extract fields of interest
                cleanedData.put("AvailabilityInStock", part.get("AvailabilityInStock"));
                cleanedData.put("AvailabilityOnOrder", part.get("AvailabilityOnOrder"));
            }
        }

        return cleanedData.isEmpty() ? null : cleanedData;
    }

}
