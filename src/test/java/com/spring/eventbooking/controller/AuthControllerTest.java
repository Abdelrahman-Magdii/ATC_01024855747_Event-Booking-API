package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.LoginRequest;
import com.spring.eventbooking.dto.Request.RegisterRequest;
import com.spring.eventbooking.dto.Response.JwtResponse;
import com.spring.eventbooking.service.AuthService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        jwtResponse = new JwtResponse();
        jwtResponse.setToken("jwt-token");
        jwtResponse.setEmail("test@example.com");
    }

    @Test
    void registerUser_ValidRequest_ReturnsJwtResponse() throws MessagingException {
        // Arrange
        ResponseEntity<JwtResponse> expectedResponse = ResponseEntity.ok(jwtResponse);
        when(authService.register(any(RegisterRequest.class), anyBoolean())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<JwtResponse> actualResponse = authController.registerUser(registerRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(jwtResponse, actualResponse.getBody());
        verify(authService, times(1)).register(registerRequest, false);
    }

    @Test
    void registerUser_MessagingException_ThrowsException() throws MessagingException {
        // Arrange
        when(authService.register(any(RegisterRequest.class), anyBoolean()))
                .thenThrow(new MessagingException("Email sending failed"));

        // Act & Assert
        assertThrows(MessagingException.class, () -> authController.registerUser(registerRequest));
    }

    @Test
    void registerAdmin_ValidRequest_ReturnsJwtResponse() throws MessagingException {
        // Arrange
        ResponseEntity<JwtResponse> expectedResponse = ResponseEntity.ok(jwtResponse);
        when(authService.register(any(RegisterRequest.class), anyBoolean())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<JwtResponse> actualResponse = authController.registerAdmin(registerRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(jwtResponse, actualResponse.getBody());
        verify(authService, times(1)).register(registerRequest, true);
    }

    @Test
    void registerAdmin_MessagingException_ThrowsException() throws MessagingException {
        // Arrange
        when(authService.register(any(RegisterRequest.class), anyBoolean()))
                .thenThrow(new MessagingException("Email sending failed"));

        // Act & Assert
        assertThrows(MessagingException.class, () -> authController.registerAdmin(registerRequest));
    }

    @Test
    void authenticateUser_ValidRequest_ReturnsJwtResponse() {
        // Arrange
        ResponseEntity<JwtResponse> expectedResponse = ResponseEntity.ok(jwtResponse);
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<JwtResponse> actualResponse = authController.authenticateUser(loginRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(jwtResponse, actualResponse.getBody());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void authenticateUser_InvalidRequest_ReturnsBadRequest() {
        // Arrange
        loginRequest.setEmail(null); // Make request invalid
        ResponseEntity<JwtResponse> expectedResponse = ResponseEntity.badRequest().build();
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<JwtResponse> actualResponse = authController.authenticateUser(loginRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }
}