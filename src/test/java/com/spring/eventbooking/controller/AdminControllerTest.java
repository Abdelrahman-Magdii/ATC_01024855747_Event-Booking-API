package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.PublishedRequest;
import com.spring.eventbooking.dto.Request.RoleUpdateRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.StatsResponse;
import com.spring.eventbooking.dto.Response.UserResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.enums.BookingStatus;
import com.spring.eventbooking.service.AdminService;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private List<UserResponse> userResponses;
    private List<Event> events;
    private List<Booking> bookings;
    private StatsResponse statsResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        userResponses = new ArrayList<>();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("testuser");
        userResponse.setEmail("test@example.com");
        userResponses.add(userResponse);

        events = new ArrayList<>();
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setPublished(true);
        events.add(event);

        bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);
        bookings.add(booking);

        statsResponse = new StatsResponse();
        statsResponse.setTotalUsers(10L);
        statsResponse.setTotalEvents(5L);
        statsResponse.setTotalBookings(20L);
        statsResponse.setPublishedEvents(3L);
        statsResponse.setPendingBookings(8L);
        statsResponse.setConfirmedBookings(10L);
        statsResponse.setCancelledBookings(2L);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Given
        when(adminService.getAllUsers()).thenReturn(userResponses);

        // When
        ResponseEntity<List<UserResponse>> response = adminController.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponses, response.getBody());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().getFirst().getId());
        assertEquals("testuser", response.getBody().getFirst().getFirstName());
        verify(adminService, times(1)).getAllUsers();
    }


    @Test
    void getAllEvents_ShouldReturnEventList() {
        // Given
        when(adminService.getAllEvents()).thenReturn(events);

        // When
        ResponseEntity<List<Event>> response = adminController.getAllEvents();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Test Event", response.getBody().get(0).getTitle());
        verify(adminService, times(1)).getAllEvents();
    }


    @Test
    void getAllBookings_ShouldReturnBookingList() {
        // Given
        when(adminService.getAllBookings()).thenReturn(bookings);

        // When
        ResponseEntity<List<Booking>> response = adminController.getAllBookings();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(BookingStatus.PENDING, response.getBody().get(0).getStatus());
        verify(adminService, times(1)).getAllBookings();
    }

    @Test
    void getSystemStats_ShouldReturnStatsResponse() {
        // Given
        when(adminService.getSystemStats()).thenReturn(statsResponse);

        // When
        ResponseEntity<StatsResponse> response = adminController.getSystemStats();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        StatsResponse result = response.getBody();
        assertNotNull(result);
        assertEquals(10L, result.getTotalUsers());
        assertEquals(5L, result.getTotalEvents());
        assertEquals(20L, result.getTotalBookings());
        assertEquals(3L, result.getPublishedEvents());
        assertEquals(8L, result.getPendingBookings());
        assertEquals(10L, result.getConfirmedBookings());
        assertEquals(2L, result.getCancelledBookings());
        verify(adminService, times(1)).getSystemStats();
    }

    @Test
    void updateUserRole_ShouldReturnSuccessResponse() {
        Long userId = 1L;
        RoleUpdateRequest roleUpdate = new RoleUpdateRequest();
        roleUpdate.setName("ADMIN");
        roleUpdate.setDescription("Administrator");

        try (MockedStatic<GlobalFunction> mockedGlobalFunction = mockStatic(GlobalFunction.class)) {

            mockedGlobalFunction.when(() -> GlobalFunction.getMS("user.role.updated"))
                    .thenReturn("User role updated successfully");

            ResponseEntity<ApiResponse> response = adminController.updateUserRole(userId, roleUpdate);

            mockedGlobalFunction.verify(() -> GlobalFunction.getMS("user.role.updated"));

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ApiResponse apiResponse = response.getBody();
            assertNotNull(apiResponse);
            assertTrue(apiResponse.isSuccess());
            assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());

            verify(adminService, times(1)).updateUserRole(userId, roleUpdate.getName(), roleUpdate.getDescription());
        }
    }

    @Test
    void publishEvent_ShouldReturnSuccessResponseWhenPublished() {
        // Given
        Long eventId = 1L;
        PublishedRequest publishDto = new PublishedRequest();
        publishDto.setPublished(true);

        try (MockedStatic<GlobalFunction> mockedGlobalFunction = mockStatic(GlobalFunction.class)) {

            mockedGlobalFunction.when(() -> GlobalFunction.getMS("event.publish.success"))
                    .thenReturn("Event published successfully");

            ResponseEntity<ApiResponse> response = adminController.publishEvent(eventId, publishDto);

            mockedGlobalFunction.verify(() -> GlobalFunction.getMS("event.publish.success"));

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ApiResponse apiResponse = response.getBody();
            assertNotNull(apiResponse);
            assertTrue(apiResponse.isSuccess());
            assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());

            verify(adminService, times(1)).setEventPublishStatus(eventId, true);
        }
    }

    @Test
    void publishEvent_ShouldReturnSuccessResponseWhenUnpublished() {
        // Given
        Long eventId = 1L;
        PublishedRequest publishDto = new PublishedRequest();
        publishDto.setPublished(false);

        try (MockedStatic<GlobalFunction> mockedGlobalFunction = mockStatic(GlobalFunction.class)) {

            mockedGlobalFunction.when(() -> GlobalFunction.getMS("event.unpublish.success"))
                    .thenReturn("Event unpublished successfully");

            // When
            ResponseEntity<ApiResponse> response = adminController.publishEvent(eventId, publishDto);

            // Debug: Verify the mock was called
            mockedGlobalFunction.verify(() -> GlobalFunction.getMS("event.unpublish.success"));

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ApiResponse apiResponse = response.getBody();
            assertTrue(apiResponse.isSuccess());
            assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());

            verify(adminService, times(1)).setEventPublishStatus(eventId, false);
        }
    }
}