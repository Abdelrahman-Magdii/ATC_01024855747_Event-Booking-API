package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.BookingRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.BookingResponse;
import com.spring.eventbooking.service.BookingService;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;


    @Mock
    private MessageSource messageSource;

    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private List<BookingResponse> bookingResponses;

    @BeforeEach
    void setUp() {
        bookingRequest = new BookingRequest();
        bookingRequest.setEventId(1L);

        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setEventId(1L);

        bookingResponses = Arrays.asList(bookingResponse);


        GlobalFunction.ms = messageSource;
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        when(bookingService.createBooking(bookingRequest)).thenReturn(bookingResponse);

        ResponseEntity<BookingResponse> response = bookingController.createBooking(bookingRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingResponse, response.getBody());
        verify(bookingService, times(1)).createBooking(bookingRequest);
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        Long bookingId = 1L;
        when(bookingService.getBookingById(bookingId)).thenReturn(bookingResponse);

        ResponseEntity<BookingResponse> response = bookingController.getBookingById(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingResponse, response.getBody());
        verify(bookingService, times(1)).getBookingById(bookingId);
    }


    @Test
    void cancelBooking_ShouldReturnCanceledBooking() {
        Long bookingId = 1L;
        when(bookingService.cancelBooking(bookingId)).thenReturn(bookingResponse);

        ResponseEntity<BookingResponse> response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingResponse, response.getBody());
        verify(bookingService, times(1)).cancelBooking(bookingId);
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        when(bookingService.getBookingsByUser()).thenReturn(bookingResponses);

        ResponseEntity<List<BookingResponse>> response = bookingController.getUserBookings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(bookingResponse, response.getBody().get(0));
        verify(bookingService, times(1)).getBookingsByUser();
    }

    @Test
    void getBookingsByEvent_ShouldReturnEventBookings() {
        Long eventId = 1L;
        when(bookingService.getBookingsByEventId(eventId)).thenReturn(bookingResponses);

        ResponseEntity<List<BookingResponse>> response = bookingController.getBookingsByEvent(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(bookingResponse, response.getBody().get(0));
        verify(bookingService, times(1)).getBookingsByEventId(eventId);
    }

    @Test
    void deleteBooking_WhenExists_ShouldReturnSuccess() {
        // Arrange
        Long bookingId = 1L;
        doNothing().when(bookingService).deleteBooking(bookingId);

        when(messageSource.getMessage(anyString(), any(), any()))
                .thenReturn("Booking deleted successfully");

        ResponseEntity<ApiResponse> response = bookingController.deleteBooking(bookingId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Booking deleted successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());

        verify(bookingService).deleteBooking(bookingId);
    }

}