package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {

    List<EventImage> findByEvent(Event event);

    Optional<EventImage> findByEventAndIsPrimaryTrue(Event event);

    void deleteByEventId(Long eventId);
}