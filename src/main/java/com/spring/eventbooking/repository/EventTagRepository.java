package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventTag;
import com.spring.eventbooking.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTagRepository extends JpaRepository<EventTag, Long> {
    List<EventTag> findByEvent(Event event);
    List<EventTag> findByTag(Tag tag);
    Optional<EventTag> findByEventAndTag(Event event, Tag tag);
    void deleteByEventAndTag(Event event, Tag tag);
}

