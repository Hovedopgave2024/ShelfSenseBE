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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
}
