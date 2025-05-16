package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Response.StatsResponse;
import com.spring.eventbooking.dto.Response.UserResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.enums.BookingStatus;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.BookingRepository;
import com.spring.eventbooking.repository.EventRepository;
import com.spring.eventbooking.repository.RoleRepository;
import com.spring.eventbooking.repository.UserRepository;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;
    private Role role;
    private Event event;
    private Booking booking;
    private List<User> userList;
    private List<Event> eventList;
    private List<Booking> bookingList;

    @BeforeEach
    void setUp() {
        // Setup test data
        role = new Role();
        role.setId(1L);
        role.setName("USER");
        role.setDescription("Regular user");

        user = new User();
        user.setId(1L);
        user.setFirstName("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setPublished(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);

        userList = Collections.singletonList(user);
        eventList = Collections.singletonList(event);
        bookingList = Collections.singletonList(booking);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponses() {
        // Given
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<UserResponse> result = adminService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user.getFirstName(), result.get(0).getFirstName());
        assertEquals(user.getEmail(), result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUserRole_ShouldUpdateUserWithExistingRole() {
        // Given
        String roleName = "ADMIN";
        String roleDescription = "Administrator";
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(roleName);
        adminRole.setDescription(roleDescription);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(adminRole));

        // When
        adminService.updateUserRole(1L, roleName, roleDescription);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(adminRole));
    }

    @Test
    void updateUserRole_ShouldCreateNewRoleIfNotExists() {
        // Given
        String roleName = "NEW_ROLE";
        String roleDescription = "New Role Description";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role savedRole = invocation.getArgument(0);
            savedRole.setId(3L);
            return savedRole;
        });

        // When
        adminService.updateUserRole(1L, roleName, roleDescription);

        // Then
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role savedRole = roleCaptor.getValue();
        assertEquals(roleName, savedRole.getName());
        assertEquals(roleDescription, savedRole.getDescription());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(1, savedUser.getRoles().size());
    }

    @Test
    void updateUserRole_ShouldUpdateRoleDescriptionIfChanged() {
        // Given
        String roleName = "ADMIN";
        String oldDescription = "Old Description";
        String newDescription = "New Description";

        Role existingRole = new Role();
        existingRole.setId(2L);
        existingRole.setName(roleName);
        existingRole.setDescription(oldDescription);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(existingRole));

        // When
        adminService.updateUserRole(1L, roleName, newDescription);

        // Then
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role updatedRole = roleCaptor.getValue();
        assertEquals(newDescription, updatedRole.getDescription());
    }

    @Test
    void updateUserRole_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        Long userId = 999L;
        String roleName = "ADMIN";
        String roleDescription = "Administrator";
        String errorMessage = "User not found with id: " + userId;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try (MockedStatic<GlobalFunction> mockedGlobalFunction = mockStatic(GlobalFunction.class)) {
            mockedGlobalFunction.when(() -> GlobalFunction.getMS(eq("user.not.found.id"), eq(userId)))
                    .thenReturn(errorMessage);

            // When & Then
            GlobalException exception = assertThrows(
                    GlobalException.class,
                    () -> adminService.updateUserRole(userId, roleName, roleDescription)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(errorMessage, exception.getMessage());

            // Verify the static method was called
            mockedGlobalFunction.verify(() -> GlobalFunction.getMS(eq("user.not.found.id"), eq(userId)));
        }

        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllEvents_ShouldReturnListOfEvents() {
        // Given
        when(eventRepository.findAll()).thenReturn(eventList);

        // When
        List<Event> result = adminService.getAllEvents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getId(), result.get(0).getId());
        assertEquals(event.getTitle(), result.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void setEventPublishStatus_ShouldUpdateEventPublishStatus() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // When
        adminService.setEventPublishStatus(1L, false);

        // Then
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertFalse(savedEvent.isPublished());
    }

    @Test
    void setEventPublishStatus_ShouldThrowExceptionWhenEventNotFound() {
        // Given
        Long eventId = 999L;
        String errorMessage = "Event not found with id: " + eventId;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        try (MockedStatic<GlobalFunction> mockedGlobalFunction = mockStatic(GlobalFunction.class)) {
            mockedGlobalFunction.when(() -> GlobalFunction.getMS(eq("event.not.found.id"), eq(eventId)))
                    .thenReturn(errorMessage);

            // When & Then
            GlobalException exception = assertThrows(
                    GlobalException.class,
                    () -> adminService.setEventPublishStatus(eventId, true)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals(errorMessage, exception.getMessage());

            // Verify the static method was called
            mockedGlobalFunction.verify(() -> GlobalFunction.getMS(eq("event.not.found.id"), eq(eventId)));
        }

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getAllBookings_ShouldReturnListOfBookings() {
        // Given
        when(bookingRepository.findAll()).thenReturn(bookingList);

        // When
        List<Booking> result = adminService.getAllBookings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getSystemStats_ShouldReturnCorrectStats() {
        // Given
        when(userRepository.count()).thenReturn(10L);
        when(eventRepository.count()).thenReturn(5L);
        when(bookingRepository.count()).thenReturn(20L);
        when(eventRepository.countByPublishedTrue()).thenReturn(3L);
        when(bookingRepository.countByStatus(BookingStatus.PENDING)).thenReturn(8L);
        when(bookingRepository.countByStatus(BookingStatus.CONFIRMED)).thenReturn(10L);
        when(bookingRepository.countByStatus(BookingStatus.CANCELLED)).thenReturn(2L);

        // When
        StatsResponse result = adminService.getSystemStats();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalUsers());
        assertEquals(5L, result.getTotalEvents());
        assertEquals(20L, result.getTotalBookings());
        assertEquals(3L, result.getPublishedEvents());
        assertEquals(8L, result.getPendingBookings());
        assertEquals(10L, result.getConfirmedBookings());
        assertEquals(2L, result.getCancelledBookings());

        verify(userRepository, times(1)).count();
        verify(eventRepository, times(1)).count();
        verify(bookingRepository, times(1)).count();
        verify(eventRepository, times(1)).countByPublishedTrue();
        verify(bookingRepository, times(1)).countByStatus(BookingStatus.PENDING);
        verify(bookingRepository, times(1)).countByStatus(BookingStatus.CONFIRMED);
        verify(bookingRepository, times(1)).countByStatus(BookingStatus.CANCELLED);
    }
}