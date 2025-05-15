package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.EventRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Event Controller")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("")
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        if (title != null) {
            return ResponseEntity.ok(eventService.findByTitle(title, pageable));
        }
        if (category != null) {
            return ResponseEntity.ok(eventService.findByCategoryName(category, pageable));
        }
        if (tag != null) {
            return ResponseEntity.ok(eventService.findByTagName(tag, pageable));
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(eventService.findByDateRange(startDate, endDate, pageable));
        }

        return ResponseEntity.ok(eventService.findAll(pageable));
    }

    @GetMapping("/published")
    public List<EventResponse> getPublishedEvents(Pageable page) {
        return eventService.getPublishedEvents(page);
    }

    @GetMapping("/user/{userId}")
    public List<EventResponse> getUserEvents(@PathVariable Long userId) {
        return eventService.getUserEvents(userId);
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PostMapping
    public EventResponse createEvent(@RequestBody EventRequest request, @RequestParam Long userId) {
        return eventService.createEvent(request, userId);
    }

    @PutMapping("/{id}")
    public EventResponse updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "event.deleted.success",
                        HttpStatus.OK.value()
                )
        );
    }


    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> publishEvent(@PathVariable Long id) {
        eventService.setPublished(id, true);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "event.publish.success",
                        HttpStatus.OK.value()
                )
        );
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> unpublishEvent(@PathVariable Long id) {
        eventService.setPublished(id, false);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "event.unpublish.success",
                        HttpStatus.OK.value()
                )
        );
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> uploadEventImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image,
            @RequestParam boolean isPrimary,
            @RequestParam String altText) throws IOException {

        eventService.uploadImage(id, image, isPrimary, altText);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "image.uploaded.success",
                        HttpStatus.OK.value()));
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) throws IOException {
        eventService.deleteImage(imageId);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "image.deleted.success",
                        HttpStatus.OK.value()));
    }

}
