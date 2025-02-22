package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Model.Supplier;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public Component createComponent(Component component, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());

        component.setUser(user);
        component.setName(textSanitizer.sanitize(component.getName()));
        component.setType(textSanitizer.sanitize(component.getType()));
        component.setFootprint(textSanitizer.sanitize(component.getFootprint()));
        component.setDesignator(component.getDesignator() != null ? textSanitizer.sanitize(component.getDesignator()): null);

        if (component.getSupplier() != null) {
            Supplier supplier = component.getSupplier();

            supplier.setName(textSanitizer.sanitize(supplier.getName()));
            supplier.setManufacturer(textSanitizer.sanitize(supplier.getManufacturer()));
            supplier.setManufacturerPart(textSanitizer.sanitize(supplier.getManufacturerPart()));
            supplier.setSupplierPart(supplier.getSupplierPart() != null ? textSanitizer.sanitize(supplier.getSupplierPart()) : null);
            supplier.setSafetyStock(supplier.getSafetyStock());
            supplier.setSafetyStockRop(supplier.getSafetyStockRop());
            supplier.setComponent(component);
        }

        Component savedComponent = componentRepository.save(component);

        savedComponent.setStockStatus(component.getStockStatus());
        savedComponent.getSupplier().setStockStatus(component.getSupplier().getStockStatus() != null ? component.getSupplier().getStockStatus() : null);

        return savedComponent;
    }

    public Component updateComponent(int id, Component updatedComponent, UserDTO userDTO) {
        Component existingComponent = componentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Component Not Found")
        );
        validateOwnership(userDTO, existingComponent);

        // Sanitize and update fields safely
        existingComponent.setName(textSanitizer.sanitize(updatedComponent.getName()));
        existingComponent.setType(textSanitizer.sanitize(updatedComponent.getType()));
        existingComponent.setFootprint(textSanitizer.sanitize(updatedComponent.getFootprint()));
        existingComponent.setPrice(updatedComponent.getPrice());
        existingComponent.setStock(updatedComponent.getStock());
        existingComponent.setSafetyStock(updatedComponent.getSafetyStock());
        existingComponent.setSafetyStockRop(updatedComponent.getSafetyStockRop());
        existingComponent.setDesignator(updatedComponent.getDesignator() != null ? textSanitizer.sanitize(updatedComponent.getDesignator()) : existingComponent.getDesignator());

        // Handle Supplier update
        if (updatedComponent.getSupplier() != null) {
            Supplier existingSupplier;

            if (existingComponent.getSupplier() == null) {
                // Create a new supplier if one does not exist
                existingSupplier = new Supplier();
                existingSupplier.setComponent(existingComponent);
                existingComponent.setSupplier(existingSupplier);
            } else {
                // Use the existing supplier
                existingSupplier = existingComponent.getSupplier();
            }

            Supplier updatedSupplier = updatedComponent.getSupplier();

            existingSupplier.setName(textSanitizer.sanitize(updatedSupplier.getName()));
            existingSupplier.setManufacturer(textSanitizer.sanitize(updatedSupplier.getManufacturer()));
            existingSupplier.setManufacturerPart(textSanitizer.sanitize(updatedSupplier.getManufacturerPart()));
            existingSupplier.setSupplierPart(textSanitizer.sanitize(updatedSupplier.getSupplierPart()));

            existingSupplier.setStock(updatedSupplier.getStock());
            existingSupplier.setIncomingStock(updatedSupplier.getIncomingStock());
            existingSupplier.setIncomingDate(updatedSupplier.getIncomingDate());
            existingSupplier.setSafetyStock(updatedSupplier.getSafetyStock());
            existingSupplier.setSafetyStockRop(updatedSupplier.getSafetyStockRop());
        }

        Component savedComponent = componentRepository.save(existingComponent);

        savedComponent.setStockStatus(updatedComponent.getStockStatus());
        savedComponent.getSupplier().setStockStatus(updatedComponent.getSupplier().getStockStatus() != null ? updatedComponent.getSupplier().getStockStatus() : null);

        return savedComponent;
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
        List<Component> components = componentRepository.findBySupplier_Name("Mouser");
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
                        component.getSupplier().getManufacturer(),
                        component.getSupplier().getManufacturerPart(),
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

                if (part.getAvailabilityInStock() > 0) {
                    component.getSupplier().setStock(part.getAvailabilityInStock());
                } else {
                    component.getSupplier().setStock(null);
                }
                List<AvailabilityOnOrderDTO> availabilityOnOrder = part.getAvailabilityOnOrder();
                if (availabilityOnOrder != null && !availabilityOnOrder.isEmpty()) {
                    AvailabilityOnOrderDTO firstOrder = availabilityOnOrder.get(0);
                    component.getSupplier().setIncomingStock(firstOrder.getQuantity());
                    component.getSupplier().setIncomingDate(firstOrder.getDate());
                } else {
                    component.getSupplier().setIncomingStock(null);
                    component.getSupplier().setIncomingDate(null);
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
    // test every minute: @Scheduled(cron = "0 * * * * ?", zone = "Europe/Copenhagen")
    // test every 10 seconds: @Scheduled(cron = "*/10 * * * * ?", zone = "Europe/Copenhagen")
    public void scheduledFetchAndUpdate() {
        System.out.println("Running scheduled component update at: " + ZonedDateTime.now(ZoneId.of("Europe/Copenhagen")));
        fetchAndUpdateComponentsWithSupplierInfo(apiKey);
        apiUpdateService.updateApiLastUpdated();
    }
}
