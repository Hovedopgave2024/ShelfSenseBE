package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.MouserApiDTO.*;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Repository.ComponentRepository;
import com.example.shelfsensebe.utility.StatusCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testFetchAndUpdateComponentsWithSupplierInfo_Returns_UpdatedComponents() {
        // Arrange
        String apiKey = "dummyApiKey";
        int userId = 1;

        Component existingComponent = getExistingComponent();

        Component mockComponent = getExistingComponent();
        when(componentRepository.findBySupplierAndUser_Id(eq(existingComponent.getSupplier()), eq(userId)))
                .thenReturn(List.of(mockComponent));

        SearchByKeywordMfrNameRequestDTO keywordRequest = new SearchByKeywordMfrNameRequestDTO(
                existingComponent.getManufacturer(),       // component.getManufacturer()
                existingComponent.getManufacturerPart(),  // component.getManufacturerPart()
                1,                                         // records
                0,                                         // PageNumber
                "",                                        // searchOptions
                ""                                         // searchWithYourSignUpLanguage
        );

        SearchByKeywordRequestBodyDTO expectedRequestBody = new SearchByKeywordRequestBodyDTO(keywordRequest);

        // Mock the WebClient chain
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);

        // Use thenAnswer() with any() to capture the argument and assert it
        when(requestBodySpec.bodyValue(any())).thenAnswer(invocation -> {
            // Directly retrieve and cast the argument passed to bodyValue()
            SearchByKeywordRequestBodyDTO requestBody = invocation.getArgument(0);

            // Perform the assertion to verify the expected value
            assertEquals(expectedRequestBody.getSearchByKeywordMfrNameRequest().getManufacturerName(),
                    requestBody.getSearchByKeywordMfrNameRequest().getManufacturerName());

            // Return the mock response spec to continue the WebClient chain
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

        System.out.println("Test testFetchAndUpdateComponentsWithSupplierInfo_Returns_UpdatedComponents passed successfully.");
    }

    @Test
    void testFetchAndUpdateComponentsWithSupplierInfoWithMissingApiKey_Returns_400_BadRequest() {
        // Arrange
        int userId = 1;

        // Mock the existing component
        Component existingComponent = getExistingComponent();
        Component mockComponent = getExistingComponent();
        when(componentRepository.findBySupplierAndUser_Id(eq(existingComponent.getSupplier()), eq(userId)))
                .thenReturn(List.of(mockComponent));

        // Create the keywordRequest with a null manufacturer to trigger a 400 error
        SearchByKeywordMfrNameRequestDTO keywordRequest = new SearchByKeywordMfrNameRequestDTO(
                existingComponent.getManufacturer(),       // component.getManufacturer()
                existingComponent.getManufacturerPart(),  // component.getManufacturerPart()
                1,                                         // records
                0,                                         // PageNumber
                "",                                        // searchOptions
                ""                                         // searchWithYourSignUpLanguage
        );

        SearchByKeywordRequestBodyDTO expectedRequestBody = new SearchByKeywordRequestBodyDTO(keywordRequest);

        // Mock the WebClient chain
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);

        // Use thenAnswer() with any() to capture the argument and assert it
        when(requestBodySpec.bodyValue(any())).thenAnswer(invocation -> {
            // Directly retrieve and cast the argument passed to bodyValue()
            SearchByKeywordRequestBodyDTO requestBody = invocation.getArgument(0);

            // Perform the assertion to verify the expected value
            assertEquals(expectedRequestBody.getSearchByKeywordMfrNameRequest().getManufacturerName(),
                    requestBody.getSearchByKeywordMfrNameRequest().getManufacturerName());

            // Return the mock response spec to continue the WebClient chain
            return requestHeadersSpec;
        });

        // create the expected error response
        List<ErrorDTO> mockErrors = List.of(
                new ErrorDTO(1, "Invalid", "Invalid unique identifier.", null, null, null, "API Key")
        );

        // Simulate an API response with errors (invalid API key)
        MouserResponseDTO mockApiResponseWithError = new MouserResponseDTO(
                mockErrors, // Pass the list of errors
                null // No search results
        );

        // Return the mock response and error
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MouserResponseDTO.class)).thenReturn(Mono.just(mockApiResponseWithError));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            // Call the service method which should throw the ResponseStatusException
            componentService.fetchAndUpdateComponentsWithSupplierInfo(null, userId); // Simulating invalid API key
        });

        // Assert that the exception is a 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        // Verify interactions
        verify(componentRepository, times(1)).findBySupplierAndUser_Id(eq(existingComponent.getSupplier()), eq(userId));
        verify(webClient, times(1)).post();
        verify(requestBodySpec, times(1)).bodyValue(any(SearchByKeywordRequestBodyDTO.class));
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(componentRepository, never()).save(any(Component.class));

        System.out.println("Test testFetchAndUpdateComponentsWithSupplierInfoWithMissingApiKey_Returns_400_BadRequest passed successfully.");
    }
}
