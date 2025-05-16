package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Response.CategoryResponse;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.dto.Response.ImageResponse;
import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventImage;
import com.spring.eventbooking.entity.Tag;
import com.spring.eventbooking.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Category Controller")
@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<EventResponse>> getCategoryEvents(@PathVariable Long id) {
        Set<Event> events = categoryService.getCategoryEvents(id);

        List<EventResponse> eventResponses = events.stream()
                .map(this::mapEventToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventResponses);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryResponse categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryResponse categoryRequest) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    EventResponse mapEventToResponse(Event event) {
        List<String> categoryNames = event.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        List<String> tagNames = event.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        List<ImageResponse> imageResponses = event.getImages().stream()
                .map(this::mapToImageResponse)
                .collect(Collectors.toList());

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .userId(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null)
                .published(event.isPublished())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .categories(categoryNames)
                .tags(tagNames)
                .images(imageResponses)
                .build();
    }

    ImageResponse mapToImageResponse(EventImage image) {
        return ImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .isPrimary(image.isPrimary()).build();
    }
}