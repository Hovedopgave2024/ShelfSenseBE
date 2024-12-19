package com.example.shelfsensebe.Controller;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserControllerTest {
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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserController userController; // Replace with your actual controller class name

    @Test
    public void testLogin_Return_200_OK() {
        // Setup mock user and credentials
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("testuser");
        mockUser.setPassword("encodedPassword");

        UserDTO expectedUserDTO = new UserDTO(1, "testuser");

        // Mock repository and password encoder behavior
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // Mock request body
        User loginRequest = new User();
        loginRequest.setName("testuser");
        loginRequest.setPassword("rawPassword");

        // Call the controller method
        ResponseEntity<UserDTO> response = userController.login(loginRequest, session);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO.getId(), response.getBody().getId());
        assertEquals(expectedUserDTO.getName(), response.getBody().getName());

        // Verify session
        verify(session, times(1)).setAttribute("user", expectedUserDTO);

        // Verify repository and encoder calls
        verify(userRepository, times(1)).findByName("testuser");
        verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");

        System.out.println("Test testLogin_Return_200_OK passed successfully.");
    }

    @Test
    public void testLogin_InvalidPassword_Return_401_Unauthorized() {
        // Setup mock user and credentials
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("testuser");
        mockUser.setPassword("encodedPassword");

        // Mock repository and password encoder behavior
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Mock request body
        User loginRequest = new User();
        loginRequest.setName("testuser");
        loginRequest.setPassword("wrongPassword");

        // Call the controller method
        ResponseEntity<UserDTO> response = userController.login(loginRequest, session);

        // Verify the results
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        // Verify repository and encoder calls
        verify(userRepository, times(1)).findByName("testuser");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(session, never()).setAttribute(eq("user"), any());

        System.out.println("Test testLogin_InvalidPassword_Return_401_Unauthorized passed successfully.");
    }

    @Test
    public void testLogin_UserNotFound_Return_401_Unauthorized() {
        // Mock repository behavior
        when(userRepository.findByName("unknownuser")).thenReturn(Optional.empty());

        // Mock request body
        User loginRequest = new User();
        loginRequest.setName("unknownuser");
        loginRequest.setPassword("anyPassword");

        // Call the controller method
        ResponseEntity<UserDTO> response = userController.login(loginRequest, session);

        // Verify the results
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        // Verify repository calls
        verify(userRepository, times(1)).findByName("unknownuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(session, never()).setAttribute(eq("user"), any());

        System.out.println("Test testLogin_UserNotFound_Return_401_Unauthorized passed successfully.");
    }
}
