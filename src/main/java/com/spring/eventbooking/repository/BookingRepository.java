package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    long countByStatus(BookingStatus status);

}