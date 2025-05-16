package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Request.LoginRequest;
import com.spring.eventbooking.dto.Request.RegisterRequest;
import com.spring.eventbooking.dto.Response.JwtResponse;
import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.mail.Services.EmailService;
import com.spring.eventbooking.mail.template.WelcomeEmailContext;
import com.spring.eventbooking.repository.RoleRepository;
import com.spring.eventbooking.repository.UserRepository;
import com.spring.eventbooking.security.JwtUtilsUser;
import com.spring.eventbooking.utiles.GlobalFunction;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private final String testToken = "valid.test.token";
    private final String testEmail = "test@example.com";
    private final String testPassword = "hashedPassword123";
    private final String testEncodedPassword = "encodedHashedPassword456";
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtilsUser jwtUtils;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private AuthService authService;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role userRole;
    private Role adminRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("encodedPassword");

        userRole = new Role();
        userRole.setName("USER");

        adminRole = new Role();
        adminRole.setName("ADMIN");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(testEmail);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword(testPassword);

        Set<Role> roles = new HashSet<>();
        Role userRole = new Role("USER");
        roles.add(userRole);
        testUser.setRoles(roles);
    }

    @Test
    void register_ShouldSuccessfullyRegisterUser() throws MessagingException {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(anyMap(), any(User.class))).thenReturn("jwtToken");

        // Act
        ResponseEntity<?> response = authService.register(registerRequest, false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwtToken", jwtResponse.getToken());
        assertEquals("test@example.com", jwtResponse.getEmail());

        verify(emailService, times(1)).sendMail(any(WelcomeEmailContext.class));
    }


    @Test
    void register_ShouldAddAdminRoleWhenIsAdmin() throws MessagingException {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(anyMap(), any(User.class))).thenReturn("jwtToken");

        // Act
        ResponseEntity<?> response = authService.register(registerRequest, true);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, never()).sendMail(any(WelcomeEmailContext.class));
    }

    @Test
    void register_ShouldThrowWhenUserRoleNotFound() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.register(registerRequest, false));
        assertEquals("role.user.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void register_ShouldThrowWhenAdminRoleNotFound() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.register(registerRequest, true));
        assertEquals("role.admin.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void register_ShouldThrowWhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setFirstName("Existing User");

        // Mock the MessageSource if your service uses it
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(), any()))
                .thenReturn("Email is already in use");

        GlobalFunction.ms = messageSource;

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.register(request, false));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email is already in use", exception.getReason());

        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void login_ShouldReturnJwtResponseWhenCredentialsValid() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(anyMap(), any(User.class))).thenReturn("jwtToken");

        // Act
        ResponseEntity<?> response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwtToken", jwtResponse.getToken());
        assertEquals("test@example.com", jwtResponse.getEmail());
    }


    @Test
    void login_ShouldThrowWhenUserNotFound() {
        try (MockedStatic<GlobalFunction> mocked = Mockito.mockStatic(GlobalFunction.class)) {
            // Arrange
            String errorMessage = "User not found";
            mocked.when(() -> GlobalFunction.getMS("user.not.found"))
                    .thenReturn(errorMessage);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> authService.login(loginRequest));
            assertEquals(errorMessage, exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }

    @Test
    void authByToken_ValidToken_ReturnsUser() {
        // Arrange
        when(jwtUtils.extractEmail(testToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(jwtUtils.isTokenValid(eq(testToken), any(User.class))).thenReturn(true);
        // Add this line since we're using testEncodedPassword in this test
        when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);

        // Act
        Optional<User> result = authService.authByToken(testToken);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(jwtUtils).extractEmail(testToken);
        verify(userRepository).findByEmail(testEmail);
        verify(jwtUtils).isTokenValid(eq(testToken), any(User.class));
        verify(passwordEncoder).encode(testPassword);
    }

    @Test
    void authByToken_InvalidToken_ReturnsEmptyOptional() {
        // Arrange
        when(jwtUtils.extractEmail(testToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(jwtUtils.isTokenValid(eq(testToken), any(User.class))).thenReturn(false);

        // Act
        Optional<User> result = authService.authByToken(testToken);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(testToken);
        verify(userRepository).findByEmail(testEmail);
        verify(jwtUtils).isTokenValid(eq(testToken), any(User.class));
    }

    @Test
    void authByToken_UserNotFound_ReturnsEmptyOptional() {
        // Arrange
        when(jwtUtils.extractEmail(testToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.authByToken(testToken);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(testToken);
        verify(userRepository).findByEmail(testEmail);
        verify(jwtUtils, never()).isTokenValid(anyString(), any(User.class));
    }

    @Test
    void authByToken_ExceptionThrown_ReturnsEmptyOptional() {
        // Arrange
        when(jwtUtils.extractEmail(testToken)).thenThrow(new RuntimeException("Token parsing error"));

        // Act
        Optional<User> result = authService.authByToken(testToken);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(testToken);
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtils, never()).isTokenValid(anyString(), any(User.class));
    }

    @Test
    void authByToken_NullToken_ReturnsEmptyOptional() {
        // Act
        Optional<User> result = authService.authByToken(null);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(null);
    }

    @Test
    void authByToken_TokenExpired_ReturnsEmptyOptional() {
        // Arrange
        String expiredToken = "expired.test.token";
        when(jwtUtils.extractEmail(expiredToken)).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(jwtUtils.isTokenValid(eq(expiredToken), any(User.class))).thenReturn(false);

        // Act
        Optional<User> result = authService.authByToken(expiredToken);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(expiredToken);
        verify(userRepository).findByEmail(testEmail);
        verify(jwtUtils).isTokenValid(eq(expiredToken), any(User.class));
    }

    @Test
    void authByToken_TokenForDifferentUser_ReturnsEmptyOptional() {
        // Arrange
        String wrongUserToken = "wrong.user.token";
        String wrongEmail = "wrong@example.com";

        when(jwtUtils.extractEmail(wrongUserToken)).thenReturn(wrongEmail);
        when(userRepository.findByEmail(wrongEmail)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.authByToken(wrongUserToken);

        // Assert
        assertTrue(result.isEmpty());
        verify(jwtUtils).extractEmail(wrongUserToken);
        verify(userRepository).findByEmail(wrongEmail);
    }
}