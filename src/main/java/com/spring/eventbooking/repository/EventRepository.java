package com.spring.eventbooking.repository;


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
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(User user);
    List<Event> findByPublishedTrue();
    List<Event> findByStartTimeAfter(LocalDateTime dateTime);
    List<Event> findByStartTimeAfterAndPublishedTrue(LocalDateTime dateTime);

    @Query("SELECT e FROM Event e JOIN e.eventCategories ec WHERE ec.category.id = :categoryId AND e.published = true")
    List<Event> findPublishedEventsByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e JOIN e.eventTags et WHERE et.tag.id = :tagId AND e.published = true")
    List<Event> findPublishedEventsByTagId(Long tagId);

}