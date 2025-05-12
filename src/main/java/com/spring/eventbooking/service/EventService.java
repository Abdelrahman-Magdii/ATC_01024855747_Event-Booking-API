package com.spring.eventbooking.service;

import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getPublishedEvents() {
        return eventRepository.findByPublishedTrue();
    }

    public List<Event> getUpcomingPublishedEvents() {
        return eventRepository.findByStartTimeAfterAndPublishedTrue(LocalDateTime.now());
    }

    public List<Event> getEventsByUser(User user) {
        return eventRepository.findByCreatedBy(user);
    }

    public List<Event> getEventsByCategory(Long categoryId) {
        return eventRepository.findPublishedEventsByCategoryId(categoryId);
    }

    public List<Event> getEventsByTag(Long tagId) {
        return eventRepository.findPublishedEventsByTagId(tagId);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}


