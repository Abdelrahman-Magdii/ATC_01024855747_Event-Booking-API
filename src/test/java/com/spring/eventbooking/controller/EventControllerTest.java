package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.EventRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.service.EventService;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private final LocalDateTime now = LocalDateTime.now();
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;
    private EventResponse mockEventResponse;
    private EventRequest mockEventRequest;
    private Pageable pageable;

    @Mock
    private MessageSource messageSource;

    private void setupMessageSource(String mess) {
        GlobalFunction.ms = messageSource;
        when(messageSource.getMessage(anyString(), any(), any()))
                .thenReturn(mess);
    }

    @BeforeEach
    void setUp() {
        mockEventResponse = EventResponse.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .location("Test Location")
                .startTime(now)
                .endTime(now.plusHours(2))
                .userId(1L)
                .published(false)
                .createdAt(now)
                .updatedAt(now)
                .categories(Collections.emptyList())
                .tags(Collections.emptyList())
                .images(Collections.emptyList())
                .build();

        mockEventRequest = new EventRequest(); // Assuming EventRequest has setters
        mockEventRequest.setTitle("Test Event");
        // Set other required fields...

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getEvents_ShouldReturnAllEvents_WhenNoFilters() {
        // Arrange
        Page<EventResponse> mockPage = new PageImpl<>(Collections.singletonList(mockEventResponse));
        when(eventService.findAll(any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<EventResponse>> response = eventController.getEvents(null, null, null, null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).findAll(pageable);
    }


    @Test
    void getEvents_ShouldFilterByTitle_WhenTitleProvided() {
        // Arrange
        Page<EventResponse> mockPage = new PageImpl<>(Collections.singletonList(mockEventResponse));
        when(eventService.findByTitle(eq("Test"), any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<EventResponse>> response = eventController.getEvents("Test", null, null, null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).findByTitle("Test", pageable);
    }

    @Test
    void getEvents_ShouldFilterByCategory_WhenCategoryProvided() {
        // Arrange
        Page<EventResponse> mockPage = new PageImpl<>(Collections.singletonList(mockEventResponse));
        when(eventService.findByCategoryName(eq("Music"), any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<EventResponse>> response = eventController.getEvents(null, "Music", null, null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).findByCategoryName("Music", pageable);
    }

    @Test
    void getEvents_ShouldFilterByTag_WhenTagProvided() {
        // Arrange
        Page<EventResponse> mockPage = new PageImpl<>(Collections.singletonList(mockEventResponse));
        when(eventService.findByTagName(eq("concert"), any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<EventResponse>> response = eventController.getEvents(null, null, "concert", null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).findByTagName("concert", pageable);
    }

    @Test
    void getEvents_ShouldFilterByDateRange_WhenDatesProvided() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(1);
        Page<EventResponse> mockPage = new PageImpl<>(Collections.singletonList(mockEventResponse));
        when(eventService.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<EventResponse>> response = eventController.getEvents(
                null, null, null, startDate, endDate, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).findByDateRange(startDate, endDate, pageable);
    }

    @Test
    void getPublishedEvents_ShouldReturnPublishedEvents() {
        // Arrange
        List<EventResponse> mockList = Collections.singletonList(mockEventResponse);
        when(eventService.getPublishedEvents(any(Pageable.class))).thenReturn(mockList);

        // Act
        List<EventResponse> result = eventController.getPublishedEvents(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventService).getPublishedEvents(pageable);
    }

    @Test
    void getUserEvents_ShouldReturnUserEvents() {
        // Arrange
        List<EventResponse> mockList = Collections.singletonList(mockEventResponse);
        when(eventService.getUserEvents(anyLong())).thenReturn(mockList);

        // Act
        List<EventResponse> result = eventController.getUserEvents(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventService).getUserEvents(1L);
    }

    @Test
    void getEventById_ShouldReturnEvent() {
        // Arrange
        when(eventService.getEventById(anyLong())).thenReturn(mockEventResponse);

        // Act
        EventResponse result = eventController.getEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventService).getEventById(1L);
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() {
        // Arrange
        when(eventService.createEvent(any(EventRequest.class), anyLong())).thenReturn(mockEventResponse);

        // Act
        EventResponse result = eventController.createEvent(mockEventRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventService).createEvent(mockEventRequest, 1L);
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() {
        // Arrange
        when(eventService.updateEvent(anyLong(), any(EventRequest.class))).thenReturn(mockEventResponse);

        // Act
        EventResponse result = eventController.updateEvent(1L, mockEventRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventService).updateEvent(1L, mockEventRequest);
    }

    @Test
    void deleteEvent_ShouldReturnSuccessResponse() {
        setupMessageSource("event.deleted.success");
        ResponseEntity<ApiResponse> response = eventController.deleteEvent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("event.deleted.success", response.getBody().getMessage());
        verify(eventService).deleteEvent(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void publishEvent_ShouldReturnSuccessResponse() {
        setupMessageSource("event.publish.success");
        ResponseEntity<ApiResponse> response = eventController.publishEvent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("event.publish.success", response.getBody().getMessage());
        verify(eventService).setPublished(1L, true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unpublishEvent_ShouldReturnSuccessResponse() {
        setupMessageSource("event.unpublish.success");
        ResponseEntity<ApiResponse> response = eventController.unpublishEvent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("event.unpublish.success", response.getBody().getMessage());
        verify(eventService).setPublished(1L, false);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadEventImage_ShouldReturnSuccessResponse() throws Exception {
        setupMessageSource("image.uploaded.success");
        MultipartFile mockFile = mock(MultipartFile.class);

        // Act
        ResponseEntity<ApiResponse> response = eventController.uploadEventImage(1L, mockFile, true, "Test Image");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("image.uploaded.success", response.getBody().getMessage());
        verify(eventService).uploadImage(1L, mockFile, true, "Test Image");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteImage_ShouldReturnSuccessResponse() throws Exception {
        setupMessageSource("image.deleted.success");
        ResponseEntity<ApiResponse> response = eventController.deleteImage(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("image.deleted.success", response.getBody().getMessage());
        verify(eventService).deleteImage(1L);
    }
}