package com.spring.eventbooking.service;

import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    public List<Booking> getBookingsByEvent(Event event) {
        return bookingRepository.findByEvent(event);
    }

    public List<Booking> getBookingsByUserAndStatus(User user, String status) {
        return bookingRepository.findByUserAndStatus(user, status);
    }

    public List<Booking> getBookingsByEventAndStatus(Event event, String status) {
        return bookingRepository.findByEventAndStatus(event, status);
    }

    public Integer countBookedTicketsByEventId(Long eventId) {
        return bookingRepository.countBookedTicketsByEventId(eventId);
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}

