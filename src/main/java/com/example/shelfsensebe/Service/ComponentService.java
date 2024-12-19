package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.StatusCalculator;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentService
{
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private StatusCalculator statusCalculator;

    @Autowired
    private TextSanitizer textSanitizer;

    public void validateOwnership(UserDTO userDTO, Component component) {
        if (userDTO == null || component == null) {
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
        component.setName(textSanitizer.sanitize(component.getName()));
        component.setType(textSanitizer.sanitize(component.getType()));
        component.setFootprint(textSanitizer.sanitize(component.getFootprint()));
        component.setManufacturerPart(textSanitizer.sanitize(component.getManufacturerPart()));
        component.setSupplier(textSanitizer.sanitize(component.getSupplier()));
        component.setDesignator(textSanitizer.sanitize(component.getDesignator()));
        component.setManufacturer(textSanitizer.sanitize(component.getManufacturer()));
        component.setSupplierPart(textSanitizer.sanitize(component.getSupplierPart()));
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

        existingComponent.setName(textSanitizer.sanitize(updatedComponent.getName()));
        existingComponent.setType(textSanitizer.sanitize(updatedComponent.getType()));
        existingComponent.setFootprint(textSanitizer.sanitize(updatedComponent.getFootprint()));
        existingComponent.setManufacturerPart(textSanitizer.sanitize(updatedComponent.getManufacturerPart()));
        existingComponent.setPrice(updatedComponent.getPrice());
        existingComponent.setSupplier(textSanitizer.sanitize(updatedComponent.getSupplier()));
        existingComponent.setStock(updatedComponent.getStock());
        existingComponent.setSafetyStock(updatedComponent.getSafetyStock());
        existingComponent.setSafetyStockRop(updatedComponent.getSafetyStockRop());
        existingComponent.setSupplierSafetyStock(updatedComponent.getSupplierSafetyStock());
        existingComponent.setSupplierSafetyStockRop(updatedComponent.getSupplierSafetyStockRop());
        existingComponent.setDesignator(textSanitizer.sanitize(updatedComponent.getDesignator()));
        existingComponent.setManufacturer(textSanitizer.sanitize(updatedComponent.getManufacturer()));
        existingComponent.setSupplierPart(textSanitizer.sanitize(updatedComponent.getSupplierPart()));

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
        System.out.println("Starting fetchAndUpdateComponentsWithSupplierInfo...");
        System.out.println("API Key: " + apiKey + ", User ID: " + userId);

        List<Component> components = componentRepository.findBySupplierAndUser_Id("Mouser", userId);
        System.out.println("Found " + components.size() + " components for user ID: " + userId);

        List<Component> updatedComponents = new ArrayList<>();

        components.forEach(component -> {
            System.out.println("Processing component with ID: " + component.getId());
            try {
                SearchByKeywordMfrNameRequestDTO keywordRequest = new SearchByKeywordMfrNameRequestDTO(
                        component.getManufacturer(),
                        component.getManufacturerPart(),
                        1,    // records
                        0,    // pageNumber
                        "",   // searchOptions
                        ""    // searchWithYourSignUpLanguage
                );
                System.out.println("Constructed keywordRequest: " + keywordRequest);

                SearchByKeywordRequestBodyDTO requestBody = new SearchByKeywordRequestBodyDTO(keywordRequest);
                System.out.println("Constructed requestBody: " + requestBody);

                MouserResponseDTO apiResponse = webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/search/keywordandmanufacturer")
                                .queryParam("apiKey", apiKey)
                                .build())
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(MouserResponseDTO.class)
                        .block();

                System.out.println("API response received: " + apiResponse);

                if (apiResponse != null && apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    System.out.println("API Errors: " + apiResponse.getErrors());
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "API Errors: " + String.join(", ", apiResponse.getErrors())
                    );
                }

                SearchResultDTO searchResults = apiResponse.getSearchResults();
                System.out.println("Search results: " + searchResults);

                if (searchResults == null || searchResults.getParts() == null || searchResults.getParts().isEmpty()) {
                    System.out.println("No search results for component ID: " + component.getId());
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No search results found for component: " + component.getId()
                    );
                }

                PartDTO part = searchResults.getParts().get(0);
                System.out.println("Processing part: " + part);

                if (part.getAvailabilityInStock() > 0) {
                    component.setSupplierStock(part.getAvailabilityInStock());
                    System.out.println("Supplier stock updated: " + part.getAvailabilityInStock());
                } else {
                    component.setSupplierStock(null);
                }

                List<AvailabilityOnOrderDTO> availabilityOnOrder = part.getAvailabilityOnOrder();
                if (availabilityOnOrder != null && !availabilityOnOrder.isEmpty()) {
                    AvailabilityOnOrderDTO firstOrder = availabilityOnOrder.get(0);
                    component.setSupplierIncomingStock(firstOrder.getQuantity());
                    component.setSupplierIncomingDate(firstOrder.getDate());
                    System.out.println("Incoming stock: " + firstOrder.getQuantity() + ", Date: " + firstOrder.getDate());
                } else {
                    component.setSupplierIncomingStock(null);
                    component.setSupplierIncomingDate(null);
                }

                component.setSupplierStockStatus(statusCalculator.calculateStatus(
                        component.getSupplierStock(),
                        component.getSupplierSafetyStock(),
                        component.getSupplierSafetyStockRop()
                ));
                System.out.println("Stock status updated for component ID: " + component.getId());

                componentRepository.save(component);
                System.out.println("Component saved: " + component);

                updatedComponents.add(component);

            } catch (Exception e) {
                System.out.println("Error processing component ID: " + component.getId());
                System.out.println("An error occurred: " + e);
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error processing component: " + component.getId(),
                        e
                );
            }
        });

        System.out.println("Completed processing all components.");
        return updatedComponents;
    }
}
