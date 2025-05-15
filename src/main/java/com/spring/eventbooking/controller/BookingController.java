package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.BookingRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.BookingResponse;
import com.spring.eventbooking.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Booking Controller")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "booking.delete.success", HttpStatus.OK.value())
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        return ResponseEntity.ok(bookingService.getBookingsByUser());
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getBookingsByEventId(eventId));
    }
}
