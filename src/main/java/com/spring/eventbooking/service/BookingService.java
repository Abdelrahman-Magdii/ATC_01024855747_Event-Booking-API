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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    public BookingResponse createBooking(BookingRequest request) {
        Event event = eventRepo.findById(request.getEventId())
                .orElseThrow(() -> new GlobalException("event.not.found", HttpStatus.NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalPrice(event.getPrice().multiply(BigDecimal.valueOf(request.getNumberOfTickets())));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBookedAt(LocalDateTime.now());

        Booking saved = bookingRepo.save(booking);
        return toResponse(saved);
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new GlobalException("booking.not.found", HttpStatus.NOT_FOUND));
        return toResponse(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepo.deleteById(id);
    }

    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new GlobalException("booking.not.found", HttpStatus.NOT_FOUND));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepo.save(booking);
        return toResponse(cancelled);
    }

    public List<BookingResponse> getBookingsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        return bookingRepo.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByEventId(Long eventId) {
        return bookingRepo.findByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
        response.setEventId(booking.getEvent().getId());
        response.setEventTitle(booking.getEvent().getTitle());
        response.setEventLocation(booking.getEvent().getLocation());
        response.setEventStartTime(booking.getEvent().getStartTime());
        response.setEventEndTime(booking.getEvent().getEndTime());
        response.setNumberOfTickets(booking.getNumberOfTickets());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setBookedAt(booking.getBookedAt());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setPaymentReference(booking.getPaymentReference());
        return response;
    }

}