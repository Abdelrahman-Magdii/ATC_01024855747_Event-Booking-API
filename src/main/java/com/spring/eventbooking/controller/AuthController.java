package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.LoginRequest;
import com.spring.eventbooking.dto.Request.RegisterRequest;
import com.spring.eventbooking.dto.Response.JwtResponse;
import com.spring.eventbooking.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Controller")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws MessagingException {
        return authService.register(registerRequest, false);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<JwtResponse> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) throws MessagingException {
        return authService.register(registerRequest, true);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}