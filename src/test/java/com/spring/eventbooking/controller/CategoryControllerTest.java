package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Response.CategoryResponse;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.dto.Response.ImageResponse;
import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventImage;
import com.spring.eventbooking.entity.Tag;
import com.spring.eventbooking.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse categoryResponse;
    private Event event;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Music");
        categoryResponse.setDescription("Music events");

        event = new Event();
        event.setId(1L);
        event.setTitle("Concert");
        event.setCategories(Set.of(new Category(1L, "Music", "Music events")));
        event.setTags(Set.of(new Tag(1L, "Live")));
        event.setImages(Set.of(new EventImage(1L, "url", "alt", true)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(List.of(categoryResponse));

        // Act
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Music", response.getBody().get(0).getName());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCategoryById_ShouldReturnCategory() {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.getCategoryById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Music", response.getBody().getName());
        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCategoryEvents_ShouldReturnEvents() {
        // Arrange
        when(categoryService.getCategoryEvents(1L)).thenReturn(Set.of(event));

        // Act
        ResponseEntity<List<EventResponse>> response = categoryController.getCategoryEvents(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Concert", response.getBody().get(0).getTitle());
        assertEquals("Music", response.getBody().get(0).getCategories().get(0));
        verify(categoryService, times(1)).getCategoryEvents(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_ShouldReturnCreatedCategory() {
        // Arrange
        when(categoryService.createCategory(categoryResponse)).thenReturn(categoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.createCategory(categoryResponse);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Music", response.getBody().getName());
        verify(categoryService, times(1)).createCategory(categoryResponse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        when(categoryService.updateCategory(1L, categoryResponse)).thenReturn(categoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = categoryController.updateCategory(1L, categoryResponse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Music", response.getBody().getName());
        verify(categoryService, times(1)).updateCategory(1L, categoryResponse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void mapEventToResponse_ShouldMapCorrectly() {
        // Arrange
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(2));
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // Act
        EventResponse response = categoryController.mapEventToResponse(event);

        // Assert
        assertEquals("Concert", response.getTitle());
        assertEquals(1, response.getCategories().size());
        assertEquals("Music", response.getCategories().get(0));
        assertEquals(1, response.getTags().size());
        assertEquals("Live", response.getTags().get(0));
        assertEquals(1, response.getImages().size());
        assertEquals("url", response.getImages().get(0).getUrl());
    }

    @Test
    void mapToImageResponse_ShouldMapCorrectly() {
        // Arrange
        EventImage image = new EventImage();
        image.setUrl("test-url");
        image.setAltText("test-alt");
        image.setPrimary(false);
        image.setEvent(event);

        // Act
        ImageResponse response = categoryController.mapToImageResponse(image);

        // Assert
        assertEquals("test-url", response.getUrl());
        assertEquals("test-alt", response.getAltText());
        assertFalse(response.isPrimary());
    }
}