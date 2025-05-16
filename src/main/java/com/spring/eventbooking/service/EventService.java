package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Request.EventRequest;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.dto.Response.ImageResponse;
import com.spring.eventbooking.entity.*;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.*;
import com.spring.eventbooking.utiles.GlobalFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final EventImageRepository eventImageRepository;
    private final FileStorageService fileStorageService;

    public Page<EventResponse> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> findByTitle(String title, Pageable pageable) {
        return eventRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> findByCategoryName(String categoryName, Pageable pageable) {
        return eventRepository.findByCategoryNameContaining(categoryName, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> findByTagName(String tagName, Pageable pageable) {
        return eventRepository.findByTagNameContaining(tagName, pageable)
                .map(this::mapToResponse);
    }

    public Page<EventResponse> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return eventRepository.findByStartTimeBetween(startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    public List<EventResponse> getPublishedEvents(Pageable pageable) {
        return eventRepository.findByPublishedTrue(pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUserEvents(Long userId) {
        return eventRepository.findEventByCreatedBy_Id(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", id), HttpStatus.NOT_FOUND));
        return mapToResponse(event);
    }

    @Transactional
    public EventResponse createEvent(EventRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("user.not.found.id", userId), HttpStatus.NOT_FOUND));

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setLocation(request.getLocation());
        event.setPublished(request.isPublished());
        event.setCreatedBy(user);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // Save the event first to get an ID
        Event savedEvent = eventRepository.save(event);

        addCategories(request, savedEvent);
        addTags(request, savedEvent);

        savedEvent = eventRepository.save(savedEvent);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = fileStorageService.saveFile(request.getImage());

                EventImage eventImage = new EventImage();
                eventImage.setEvent(savedEvent);
                eventImage.setUrl(imageUrl);
                eventImage.setPrimary(true);
                eventImage.setAltText(request.getTitle());

                eventImageRepository.save(eventImage);
            } catch (IOException e) {
                throw new GlobalException("image.upload.filed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return mapToResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", id), HttpStatus.NOT_FOUND));

        updateEntity(request, existingEvent);

        addCategories(request, existingEvent);
        addTags(request, existingEvent);

        Event updatedEvent = eventRepository.save(existingEvent);
        return mapToResponse(updatedEvent);
    }

    public void addCategories(EventRequest request, Event existingEvent) {
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (String tagName : request.getCategories()) {
                Category category = categoryRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(tagName);
                            return categoryRepository.save(newCategory);
                        });
                categories.add(category);
            }
            existingEvent.setCategories(categories);
        }
    }

    public void addTags(EventRequest request, Event existingEvent) {
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            existingEvent.setTags(tags);
        }
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", id), HttpStatus.NOT_FOUND));

        eventImageRepository.deleteByEventId(id);
        eventRepository.delete(event);
    }

    public void setPublished(Long id, boolean published) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", id), HttpStatus.NOT_FOUND));
        event.setPublished(published);
        eventRepository.save(event);
    }

    public void uploadImage(Long eventId, MultipartFile image, boolean isPrimary, String altText) throws IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", eventId), HttpStatus.NOT_FOUND));

        String imageUrl = fileStorageService.saveFile(image);

        if (isPrimary) {
            eventImageRepository.updatePrimaryStatusForEvent(eventId, false);
        }

        EventImage eventImage = new EventImage();
        eventImage.setEvent(event);
        eventImage.setUrl(imageUrl);
        eventImage.setPrimary(isPrimary);
        eventImage.setAltText(altText);

        eventImageRepository.save(eventImage);
    }

    public void deleteImage(Long imageId) throws IOException {
        EventImage image = eventImageRepository.findById(imageId)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("image.not.found.id", imageId), HttpStatus.NOT_FOUND));

        fileStorageService.deleteFile(image.getUrl());
        eventImageRepository.delete(image);
    }

    private void updateEntity(EventRequest request, Event event) {
        Optional.ofNullable(request.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(request.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(request.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(request.getStartTime()).ifPresent(event::setStartTime);
        Optional.ofNullable(request.getEndTime()).ifPresent(event::setEndTime);
        Optional.ofNullable(request.getCapacity()).ifPresent(event::setCapacity);
        Optional.ofNullable(request.getPrice()).ifPresent(event::setPrice);
        event.setPublished(request.isPublished());
        event.setUpdatedAt(LocalDateTime.now());
    }

    public EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .userId(event.getCreatedBy().getId())
                .published(event.isPublished())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .categories(event.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList()))
                .tags(event.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .images(event.getImages().stream()
                        .map(img -> ImageResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .isPrimary(img.isPrimary())
                                .altText(img.getAltText())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}