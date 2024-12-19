package com.example.shelfsensebe.Service;
import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.utility.StatusCalculator;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ComponentServiceTest {

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private StatusCalculator statusCalculator;

    @InjectMocks
    private ComponentService componentService;

    private Component getExistingComponent() {
        Component existingComponent = new Component();
        existingComponent.setId(1);
        existingComponent.setName("test");
        existingComponent.setType("hello");
        existingComponent.setFootprint("world");
        existingComponent.setManufacturer("Siemens");
        existingComponent.setManufacturerPart("6ED10521CC080BA2");
        existingComponent.setPrice(2.0);
        existingComponent.setSupplier("Mouser");
        existingComponent.setStock(10);
        existingComponent.setSafetyStock(10);
        existingComponent.setSupplierSafetyStock(5);
        existingComponent.setSupplierSafetyStockRop(3);
        existingComponent.setSupplierStock(null);
        existingComponent.setSupplierIncomingDate(null);
        existingComponent.setSupplierIncomingStock(null);
        return existingComponent;
    }

    @Test
    void testFetchAndUpdateComponentsWithSupplierInfo() {
        // Arrange
        String apiKey = "dummyApiKey";
        int userId = 1;

        Component mockComponent = getExistingComponent();
        when(componentRepository.findBySupplierAndUser_Id(eq("Mouser"), eq(userId)))
                .thenReturn(List.of(mockComponent));

        SearchByKeywordRequestBodyDTO expectedRequestBody = new SearchByKeywordRequestBodyDTO(
                new SearchByKeywordMfrNameRequestDTO(
                        "Siemens",              // component.getManufacturer()
                        "6ED10521CC080BA2",     // component.getManufacturerPart()
                        1,                      // records
                        0,                      // pageNumber
                        "",                     // searchOptions
                        ""                      // searchWithYourSignUpLanguage
                )
        );

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);

        when(requestBodySpec.bodyValue(any())).thenAnswer(invocation -> {
            Object argument = invocation.getArgument(0);

            if (argument instanceof SearchByKeywordRequestBodyDTO requestBody) {
                System.out.println("bodyValue() called with: " + requestBody);
                // Optionally validate specific fields if needed
                assertEquals(expectedRequestBody.getSearchByKeywordMfrNameRequest().getManufacturerName(),
                        requestBody.getSearchByKeywordMfrNameRequest().getManufacturerName());
            }

            return requestHeadersSpec;
        });

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        MouserResponseDTO mockApiResponse = new MouserResponseDTO(
                Collections.emptyList(),
                new SearchResultDTO(
                        1,
                        List.of(new PartDTO(
                                "Siemens",
                                "6ED10521CC080BA2",
                                16,
                                List.of(new AvailabilityOnOrderDTO(3, Date.valueOf("2024-12-10")))
                        ))
                )
        );

        when(responseSpec.bodyToMono(MouserResponseDTO.class)).thenReturn(Mono.just(mockApiResponse));

        when(statusCalculator.calculateStatus(16, 5, 3)).thenReturn(4);

        // Act
        List<Component> updatedComponents = componentService.fetchAndUpdateComponentsWithSupplierInfo(apiKey, userId);

        // Assert
        assertEquals(1, updatedComponents.size());
        Component updatedComponent = updatedComponents.get(0);
        assertEquals(16, updatedComponent.getSupplierStock());
        assertEquals(3, updatedComponent.getSupplierIncomingStock());
        assertEquals(Date.valueOf("2024-12-10"), updatedComponent.getSupplierIncomingDate());
        assertEquals(4, updatedComponent.getSupplierStockStatus());

        // Verify interactions
        verify(componentRepository, times(1)).findBySupplierAndUser_Id("Mouser", userId); // Correct verify
        verify(webClient, times(1)).post(); // Correct verify
        verify(requestBodySpec, times(1)).bodyValue(any(SearchByKeywordRequestBodyDTO.class)); // Correct verify
        verify(componentRepository, times(1)).save(any(Component.class));

        System.out.println("Test testFetchApiAndUpdateUserComponentsData_Returns_500_OnError passed successfully.");
    }
}
