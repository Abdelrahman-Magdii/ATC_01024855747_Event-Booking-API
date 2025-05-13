package com.spring.eventbooking.repository;


import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.Tag;
import com.spring.eventbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPublishedTrue();

    List<Event> findByCreatedBy(User user);

    List<Event> findByCategoriesIn(List<Category> categories);

    List<Event> findByTagsIn(List<Tag> tags);

    @Query("SELECT e FROM Event e WHERE e.startTime > :now AND e.published = true")
    List<Event> findUpcomingEvents(LocalDateTime now);
}