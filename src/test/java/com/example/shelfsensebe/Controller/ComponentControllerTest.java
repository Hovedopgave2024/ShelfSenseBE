package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.Component;
import com.example.shelfsensebe.Service.ApiUpdateService;
import com.example.shelfsensebe.Service.ComponentService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentControllerTest {

    @Mock
    private HttpSession session;

    @InjectMocks
    private ComponentController componentController;

    @Mock
    private ComponentService componentService;

    @Mock
    private ApiUpdateService apiUpdateService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

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
        existingComponent.setSupplierStock(null);
        existingComponent.setSupplierIncomingDate(null);
        existingComponent.setSupplierIncomingStock(null);
        return existingComponent;
    }

    @Test
    public void testFetchApiAndUpdateUserComponentsData_Returns_UpdatedComponent() {
        // Mock data setup
        String userId = "1";
        String apiKey = "291020DummyApi";
        UserDTO mockUser = new UserDTO(1, "Test User");

        // Mock the existing component
        Component existingComponent = getExistingComponent();

        // Mocking session behavior
        when(session.getAttribute("user")).thenReturn(mockUser);

        // Mocking componentService to update fields on the existing component
        when(componentService.fetchAndUpdateComponentsWithSupplierInfo(apiKey, 1)).thenAnswer(invocation -> {
            // Simulate updating the existingComponent
            existingComponent.setSupplierStock(16);
            existingComponent.setSupplierIncomingDate(Date.valueOf("2025-02-13"));
            existingComponent.setSupplierIncomingStock(3);
            return Collections.singletonList(existingComponent);
        });

        // Mock apiUpdateService behavior
        doNothing().when(apiUpdateService).updateApiLastUpdated(1);

        // Creating request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("apiKey", apiKey);

        // Invoking the controller method
        ResponseEntity<List<Component>> response = componentController.fetchApiAndUpdateUserComponentsData(requestBody, session);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        Component responseComponent = response.getBody().get(0);
        assertEquals("Siemens", responseComponent.getManufacturer());
        assertEquals("6ED10521CC080BA2", responseComponent.getManufacturerPart());

        // Assert updated fields
        assertEquals(16, responseComponent.getSupplierStock());
        assertEquals(3, responseComponent.getSupplierIncomingStock());
        assertEquals(Date.valueOf("2025-02-13"), responseComponent.getSupplierIncomingDate());

        // Verify mocks
        verify(session, times(1)).getAttribute("user");
        verify(componentService, times(1)).fetchAndUpdateComponentsWithSupplierInfo(apiKey, 1);
        verify(apiUpdateService, times(1)).updateApiLastUpdated(1);

        System.out.println("Test testFetchApiAndUpdateUserComponentsData_Returns_200_Ok_WithMockedUpdate passed successfully.");
    }

    @Test
    public void testFetchApiAndUpdateUserComponentsData_Returns_401_Unauthorized() {

        String userId = "1";
        String apiKey = "291020DummyApi";

        // Mock the existing component
        Component existingComponent = getExistingComponent();

        // session is null
        when(session.getAttribute("user")).thenReturn(null);

        when(componentService.fetchAndUpdateComponentsWithSupplierInfo(apiKey, 1)).thenAnswer(invocation -> {
            // Simulate updating the existingComponent
            existingComponent.setSupplierStock(16);
            existingComponent.setSupplierIncomingDate(Date.valueOf("2025-02-13"));
            existingComponent.setSupplierIncomingStock(3);
            return Collections.singletonList(existingComponent);
        });

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("apiKey", apiKey);

        ResponseEntity<List<Component>> response = componentController.fetchApiAndUpdateUserComponentsData(requestBody, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        verify(session, times(1)).getAttribute("user");
        verify(componentService, never()).fetchAndUpdateComponentsWithSupplierInfo(apiKey, 1);

        System.out.println("Test testFetchApiAndUpdateUserComponentsData_Returns_401_Unauthorized passed successfully.");
    }

    @Test
    public void testFetchApiAndUpdateUserComponentsData_Returns_500_OnError() {
        // Mock data setup
        String userId = "1";
        UserDTO mockUser = new UserDTO(1, "Test User");

        // Mocking session and service behavior
        when(session.getAttribute("user")).thenReturn(mockUser);
        when(componentService.fetchAndUpdateComponentsWithSupplierInfo(null, 1))
                .thenThrow(new RuntimeException("Simulated error fetching API"));

        // Creating request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("apiKey", null); // Simulating a null apiKey to trigger the error

        // Invoking the controller method
        ResponseEntity<List<Component>> response = componentController.fetchApiAndUpdateUserComponentsData(requestBody, session);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify mocks
        verify(session, times(1)).getAttribute("user");
        verify(componentService, times(1)).fetchAndUpdateComponentsWithSupplierInfo(null, 1);

        System.out.println("Test testFetchApiAndUpdateUserComponentsData_Returns_500_OnError passed successfully.");
    }
}
