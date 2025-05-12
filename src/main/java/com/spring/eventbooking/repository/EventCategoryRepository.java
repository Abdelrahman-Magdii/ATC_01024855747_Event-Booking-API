package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findByEvent(Event event);
    List<EventCategory> findByCategory(Category category);
    Optional<EventCategory> findByEventAndCategory(Event event, Category category);
    void deleteByEventAndCategory(Event event, Category category);
}

