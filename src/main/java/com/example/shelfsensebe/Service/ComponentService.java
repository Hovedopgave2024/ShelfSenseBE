package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.StatusCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

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
                SearchByKeywordMfrNameRequestDTO keywordRequest = new SearchByKeywordMfrNameRequestDTO(
                        component.getManufacturer(),
                        component.getManufacturerPart(),
                        1,    // records
                        0,    // pageNumber
                        "",   // searchOptions
                        ""    // searchWithYourSignUpLanguage
                );
                SearchByKeywordRequestBodyDTO requestBody = new SearchByKeywordRequestBodyDTO(keywordRequest);

                MouserResponseDTO apiResponse = webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/search/keywordandmanufacturer")
                                .queryParam("apiKey", apiKey)
                                .build())
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(MouserResponseDTO.class)
                        .block();

                if (apiResponse != null && apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "API Errors: " + String.join(", ", apiResponse.getErrors())
                    );
                }

                SearchResultDTO searchResults = apiResponse.getSearchResults();

                if (searchResults == null || searchResults.getParts() == null || searchResults.getParts().isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No search results found for component: " + component.getId()
                    );
                }

                PartDTO part = searchResults.getParts().get(0);
                if (part.getAvailabilityInStock() > 0) {
                    component.setSupplierStock(part.getAvailabilityInStock());
                } else {
                    component.setSupplierStock(null);
                }
                List<AvailabilityOnOrderDTO> availabilityOnOrder = part.getAvailabilityOnOrder();
                if (availabilityOnOrder != null && !availabilityOnOrder.isEmpty()) {
                    AvailabilityOnOrderDTO firstOrder = availabilityOnOrder.get(0);
                    component.setSupplierIncomingStock(firstOrder.getQuantity());
                    component.setSupplierIncomingDate(firstOrder.getDate());
                } else {
                    component.setSupplierIncomingStock(null);
                    component.setSupplierIncomingDate(null);
                }

                component.setSupplierStockStatus(statusCalculator.calculateStatus(
                        component.getSupplierStock(),
                        component.getSupplierSafetyStock(),
                        component.getSupplierSafetyStockRop()
                ));
                componentRepository.save(component);
                updatedComponents.add(component);

            } catch(Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error processing component: " + component.getId(),
                        e
                );
            }
        });

        return updatedComponents;
    }

}
