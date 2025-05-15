package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Event;
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
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e JOIN e.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    Page<Event> findByCategoryNameContaining(@Param("category") String categoryName, Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e JOIN e.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%'))")
    Page<Event> findByTagNameContaining(@Param("tag") String tagName, Pageable pageable);


    Page<Event> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Event> findByPublishedTrue(Pageable pageable);

    List<Event> findEventByCreatedBy_Id(Long userId);

    long countByPublishedTrue();
}