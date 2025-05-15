package com.spring.eventbooking.repository;

import com.spring.eventbooking.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE EventImage ei SET ei.isPrimary = :isPrimary WHERE ei.event.id = :eventId")
    void updatePrimaryStatusForEvent(@Param("eventId") Long eventId, @Param("isPrimary") boolean isPrimary);

    void deleteByEventId(Long eventId);
}