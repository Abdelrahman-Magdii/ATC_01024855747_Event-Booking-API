package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Response.CategoryResponse;
import com.spring.eventbooking.entity.Category;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.CategoryRepository;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CategoryService categoryService;

    private void setupMessageSource() {
        GlobalFunction.ms = messageSource;
        when(messageSource.getMessage(anyString(), any(), any()))
                .thenReturn("Mocked message");
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        Category category1 = new Category(1L, "Music", "Music events");
        Category category2 = new Category(2L, "Sports", "Sports events");
        List<Category> mockCategories = List.of(category1, category2);

        when(categoryRepository.findAll()).thenReturn(mockCategories);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(2, result.size());

        CategoryResponse firstResponse = result.get(0);
        assertEquals(1L, firstResponse.getId());
        assertEquals("Music", firstResponse.getName());
        assertEquals("Music events", firstResponse.getDescription());

        CategoryResponse secondResponse = result.get(1);
        assertEquals(2L, secondResponse.getId());
        assertEquals("Sports", secondResponse.getName());
        assertEquals("Sports events", secondResponse.getDescription());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Music");
        category.setDescription("Music events");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Music", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        setupMessageSource();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            categoryService.getCategoryById(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryEvents_ShouldReturnEvents_WhenCategoryExists() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Music");
        category.setDescription("Music events");
        Event event = new Event();
        event.setId(1L);
        category.setEvents(Set.of(event));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Set<Event> result = categoryService.getCategoryEvents(1L);

        // Assert
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void createCategory_ShouldSaveNewCategory() {
        // Arrange
        CategoryResponse request = new CategoryResponse();
        request.setName("Music");
        request.setDescription("Music events");

        when(categoryRepository.existsByName("Music")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Act
        CategoryResponse result = categoryService.createCategory(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Music", result.getName());
        verify(categoryRepository, times(1)).existsByName("Music");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenNameExists() {
        setupMessageSource();

        CategoryResponse request = new CategoryResponse();
        request.setName("Music");
        request.setDescription("Music events");

        when(categoryRepository.existsByName("Music")).thenReturn(true);

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            categoryService.createCategory(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(categoryRepository, times(1)).existsByName("Music");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateExistingCategory() {
        // Arrange
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");

        CategoryResponse request = new CategoryResponse();
        request.setName("New Name");
        request.setDescription("New Description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("New Name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, request);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("New Name");
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenNewNameExists() {
        setupMessageSource();

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");

        CategoryResponse request = new CategoryResponse();
        request.setName("Existing Name");
        request.setDescription("New Description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("Existing Name")).thenReturn(true);

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            categoryService.updateCategory(1L, request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Existing Name");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDelete_WhenNoEvents() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Music");
        category.setDescription("Music events");
        category.setEvents(Collections.emptySet());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenHasEvents() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Music");
        category.setDescription("Music events");

        Event event = new Event();
        category.setEvents(Set.of(event));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}