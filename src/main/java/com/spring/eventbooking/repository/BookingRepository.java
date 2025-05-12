package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByEventId(Long eventId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.event.id = :eventId")
    List<Booking> findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT SUM(b.numberOfTickets) FROM Booking b WHERE b.event.id = :eventId AND b.status != 'CANCELLED'")
    Integer sumTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b WHERE b.event.startTime BETWEEN :start AND :end")
    List<Booking> findByEventStartTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Booking> findByUser(User user);

    List<Booking> findByEvent(Event event);

    List<Booking> findByUserAndStatus(User user, String status);

    List<Booking> findByEventAndStatus(Event event, String status);

    Integer countBookedTicketsByEventId(Long eventId);
}