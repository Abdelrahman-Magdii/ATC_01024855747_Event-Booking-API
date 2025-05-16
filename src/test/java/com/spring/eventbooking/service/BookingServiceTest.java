package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Request.BookingRequest;
import com.spring.eventbooking.dto.Response.BookingResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.enums.BookingStatus;
import com.spring.eventbooking.enums.PaymentStatus;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.BookingRepository;
import com.spring.eventbooking.repository.EventRepository;
import com.spring.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Event testEvent;
    private Booking testBooking;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        // Setup test event
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setLocation("Test Location");
        testEvent.setStartTime(LocalDateTime.now().plusDays(1));
        testEvent.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testEvent.setPrice(new BigDecimal("50.00"));

        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setEvent(testEvent);
        testBooking.setNumberOfTickets(2);
        testBooking.setTotalPrice(new BigDecimal("100.00"));
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking.setPaymentStatus(PaymentStatus.PENDING);
        testBooking.setBookedAt(LocalDateTime.now());

        // Setup booking request
        bookingRequest = new BookingRequest();
        bookingRequest.setEventId(1L);
        bookingRequest.setNumberOfTickets(2);
    }

    @Test
    void createBooking_Success() {
        // Setup SecurityContext for this test
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        BookingResponse response = bookingService.createBooking(bookingRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("John Doe", response.getUserName());
        assertEquals(1L, response.getEventId());
        assertEquals("Test Event", response.getEventTitle());
        assertEquals(2, response.getNumberOfTickets());
        assertEquals(new BigDecimal("100.00"), response.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        verify(eventRepository).findById(1L);
        verify(userRepository).findByEmail("test@example.com");
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_EventNotFound() {
        // Setup SecurityContext for this test
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            bookingService.createBooking(bookingRequest);
        });

        assertEquals("event.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(eventRepository).findById(1L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getBookingById_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // When
        BookingResponse response = bookingService.getBookingById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Event", response.getEventTitle());
        assertEquals(2, response.getNumberOfTickets());

        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            bookingService.getBookingById(1L);
        });

        assertEquals("booking.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(bookingRepository).findById(1L);
    }

    @Test
    void deleteBooking_Success() {
        // When
        bookingService.deleteBooking(1L);

        // Then
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void cancelBooking_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        BookingResponse response = bookingService.cancelBooking(1L);

        // Then
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void cancelBooking_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            bookingService.cancelBooking(1L);
        });

        assertEquals("booking.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingsByUser_Success() {
        // Setup SecurityContext for this test
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Given
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(testBooking));

        // When
        List<BookingResponse> responses = bookingService.getBookingsByUser();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Test Event", responses.get(0).getEventTitle());

        verify(userRepository).findByEmail("test@example.com");
        verify(bookingRepository).findByUserId(1L);
    }

    @Test
    void getBookingsByEventId_Success() {
        // Given
        when(bookingRepository.findByEventId(1L)).thenReturn(Arrays.asList(testBooking));

        // When
        List<BookingResponse> responses = bookingService.getBookingsByEventId(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("Test Event", responses.get(0).getEventTitle());

        verify(bookingRepository).findByEventId(1L);
    }
}