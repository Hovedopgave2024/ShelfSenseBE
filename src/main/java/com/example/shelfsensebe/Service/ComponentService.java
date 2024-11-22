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

        // Aggregate API responses
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

            aggregatedResponses.put(component.getManufacturerPart(), apiResponse);
        });

        return aggregatedResponses;
    }

}
