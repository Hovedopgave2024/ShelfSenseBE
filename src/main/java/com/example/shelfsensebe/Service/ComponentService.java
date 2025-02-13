package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class ComponentService
{
    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private TextSanitizer textSanitizer;

    @Autowired
    private ApiUpdateService apiUpdateService;

    @Value("${apiKey}")
    String apiKey;

    public void validateOwnership(UserDTO userDTO, Component component) {
        if (userDTO == null || component == null) {
            throw new IllegalArgumentException("Unauthorized access: You do not own this component.");
        }
    }

    public Component createComponent(Component component, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());

        component.setUser(user);
        component.setName(textSanitizer.sanitize(component.getName()));
        component.setType(textSanitizer.sanitize(component.getType()));
        component.setFootprint(textSanitizer.sanitize(component.getFootprint()));
        component.setManufacturerPart(textSanitizer.sanitize(component.getManufacturerPart()));
        component.setManufacturer(textSanitizer.sanitize(component.getManufacturer()));
        component.setSupplier(textSanitizer.sanitize(component.getSupplier()));
        component.setDesignator(component.getDesignator() != null ? textSanitizer.sanitize(component.getDesignator()): null);
        component.setSupplierPart(component.getSupplierPart() != null ? textSanitizer.sanitize(component.getSupplierPart()): null);

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
        existingComponent.setManufacturer(textSanitizer.sanitize(updatedComponent.getManufacturer()));
        existingComponent.setPrice(updatedComponent.getPrice());
        existingComponent.setSupplier(textSanitizer.sanitize(updatedComponent.getSupplier()));
        existingComponent.setStock(updatedComponent.getStock());
        existingComponent.setSafetyStock(updatedComponent.getSafetyStock());
        existingComponent.setSafetyStockRop(updatedComponent.getSafetyStockRop());
        existingComponent.setSupplierSafetyStock(updatedComponent.getSupplierSafetyStock());
        existingComponent.setSupplierSafetyStockRop(updatedComponent.getSupplierSafetyStockRop());
        existingComponent.setDesignator(updatedComponent.getDesignator() != null ? textSanitizer.sanitize(updatedComponent.getDesignator()): null);
        existingComponent.setSupplierPart(updatedComponent.getSupplierPart() != null ? textSanitizer.sanitize(updatedComponent.getSupplierPart()): null);
        return componentRepository.save(existingComponent);
    }

    public void deleteComponent(int id, UserDTO userDTO) {
        Component component = componentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Component not found")
        );
        validateOwnership(userDTO, component);
        componentRepository.delete(component);
    }

    public List<Component> fetchAndUpdateComponentsWithSupplierInfo(String apiKey) {
        // Find components with supplier = Mouser and only fetch the rows in ComponentSupplierDTO
        List<Component> components = componentRepository.findBySupplier("Mouser");
        List<Component> updatedComponents = new ArrayList<>();

        // Control the 30 API calls limit per minute
        int apiCallCount = 0;
        long lastBatchStartTime = System.currentTimeMillis();

        for (Component component : components) {
            try {

                if (apiCallCount > 0 && apiCallCount % 29 == 0) {
                    long elapsedTime = System.currentTimeMillis() - lastBatchStartTime; // Time since last batch start
                    long remainingTime = 65000 - elapsedTime; // Calculate remaining time to complete 1 minute and 5 second buffer

                    if (remainingTime > 0) {
                        Thread.sleep(remainingTime); // Wait only for the remaining time
                    }
                    lastBatchStartTime = System.currentTimeMillis(); // Reset batch start time
                }

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
                        .timeout(Duration.ofSeconds(8))
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                                        .filter(throwable -> throwable instanceof TimeoutException))
                        .block();

                if (apiResponse == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No response from the API"
                    );
                }

                if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                    StringBuilder errorMessages = new StringBuilder();

                    for (ErrorDTO error : apiResponse.getErrors()) {
                        errorMessages.append("Error Code: ").append(error.getCode())
                                .append(", Message: ").append(error.getMessage())
                                .append(", Property: ").append(error.getPropertyName())
                                .append("\n");

                        if ("API Key".equals(error.getPropertyName())) {
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Invalid API key detected! Stopping processing."
                            );
                        }
                    }
                    System.out.println("API Errors for component with id " + component.getId() + ": \n" + errorMessages);
                    continue;
                }

                SearchResultDTO searchResults = apiResponse.getSearchResults();

                if (searchResults.getParts() == null || searchResults.getParts().isEmpty()) {
                    continue;
                }


                PartDTO part = searchResults.getParts().get(0);

                if (
                        part.getAvailabilityInStock() == component.getSupplierStock() &&
                        (part.getAvailabilityOnOrder() == null || part.getAvailabilityOnOrder().isEmpty()) &&
                        component.getSupplierIncomingStock() == null && component.getSupplierIncomingDate() == null
                ) {
                    continue;
                } else if (
                        part.getAvailabilityOnOrder() != null && !part.getAvailabilityOnOrder().isEmpty() &&
                                part.getAvailabilityInStock() == component.getSupplierStock() &&
                                part.getAvailabilityOnOrder().get(0).getQuantity() == component.getSupplierIncomingStock() &&
                                part.getAvailabilityOnOrder().get(0).getDate() == component.getSupplierIncomingDate()
                ) {
                    continue;
                }


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

                updatedComponents.add(component);

                // Add count to api counter
                apiCallCount++;

            } catch (ResponseStatusException e) {
                // Catch and log the ResponseStatusException
                System.out.println("Caught ResponseStatusException: " + e.getStatusCode() + " " + e.getReason());
                throw e;

            } catch (Exception e) {
                // Handle unexpected exceptions
                System.out.println("Caught unexpected exception: " + e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", e);
            }
        }

        componentRepository.saveAll(updatedComponents);

        return updatedComponents;
    }

    @Scheduled(cron = "0 0 2 * * ?", zone = "Europe/Copenhagen")
    // test every minute:
    // @Scheduled(cron = "0 * * * * ?", zone = "Europe/Copenhagen")
    public void scheduledFetchAndUpdate() {
        System.out.println("Running scheduled component update at: " + ZonedDateTime.now(ZoneId.of("Europe/Copenhagen")));
        fetchAndUpdateComponentsWithSupplierInfo(apiKey);
        apiUpdateService.updateApiLastUpdated();
    }
}
