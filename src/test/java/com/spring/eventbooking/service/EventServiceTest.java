package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Request.EventRequest;
import com.spring.eventbooking.dto.Response.EventResponse;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.EventImage;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.*;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private EventImageRepository eventImageRepository;
    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Event testEvent;
    private EventRequest eventRequest;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Description");
        testEvent.setLocation("Location");
        testEvent.setStartTime(LocalDateTime.now());
        testEvent.setEndTime(LocalDateTime.now().plusHours(2));
        testEvent.setCreatedBy(testUser);
        testEvent.setCreatedAt(LocalDateTime.now());
        testEvent.setUpdatedAt(LocalDateTime.now());
        testEvent.setPublished(true);
        testEvent.setCategories(new HashSet<>());
        testEvent.setTags(new HashSet<>());
        testEvent.setImages(new HashSet<>());

        eventRequest = new EventRequest();
        eventRequest.setTitle("Test Event");
        eventRequest.setDescription("Description");
        eventRequest.setLocation("Location");
        eventRequest.setStartTime(LocalDateTime.now());
        eventRequest.setEndTime(LocalDateTime.now().plusHours(2));
        eventRequest.setPublished(true);
        eventRequest.setCapacity(100);
        eventRequest.setPrice(BigDecimal.valueOf(50.0));
    }

    private void setupMessageSource() {
        GlobalFunction.ms = messageSource;
        when(messageSource.getMessage(anyString(), any(), any()))
                .thenReturn("Mocked message");
    }


    @Test
    void testAddCategories_ShouldSaveNewCategoryIfNotFound() {
        EventRequest request = new EventRequest();
        request.setCategories(List.of("Tech", "Science"));
        Event event = new Event();

        when(categoryRepository.findByName("Tech")).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Science")).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        eventService.addCategories(request, event);

        assertEquals(2, event.getCategories().size());
        verify(categoryRepository, times(2)).save(any());
    }

    @Test
    void testUploadImage_ShouldSaveImageAndSetPrimary() throws IOException {
        Long eventId = 1L;
        MultipartFile image = mock(MultipartFile.class);
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(fileStorageService.saveFile(image)).thenReturn("image.jpg");

        eventService.uploadImage(eventId, image, true, "Sample Alt");

        verify(fileStorageService).saveFile(image);
        verify(eventImageRepository).updatePrimaryStatusForEvent(eventId, false);
        verify(eventImageRepository).save(any(EventImage.class));
    }

    @Test
    void testDeleteImage_ShouldDeleteImageIfFound() throws IOException {
        Long imageId = 10L;
        EventImage eventImage = new EventImage();
        eventImage.setId(imageId);
        eventImage.setUrl("image.png");

        when(eventImageRepository.findById(imageId)).thenReturn(Optional.of(eventImage));

        eventService.deleteImage(imageId);

        verify(fileStorageService).deleteFile("image.png");
        verify(eventImageRepository).delete(eventImage);
    }

    @Test
    void testDeleteImage_ShouldThrowExceptionIfNotFound() {
        setupMessageSource();
        Long imageId = 999L;
        when(eventImageRepository.findById(imageId)).thenReturn(Optional.empty());

        GlobalException ex = assertThrows(GlobalException.class, () -> {
            eventService.deleteImage(imageId);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void testGetEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        EventResponse response = eventService.getEventById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Event");
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {

        setupMessageSource();
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void testCreateEvent_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(2L); // Simulate DB setting ID
            return event;
        });

        EventResponse response = eventService.createEvent(eventRequest, 1L);

        assertThat(response.getTitle()).isEqualTo(eventRequest.getTitle());
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(eventRepository, atLeastOnce()).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_NotFound() {
        setupMessageSource();
        when(eventRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(404L, eventRequest))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void testDeleteEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        eventService.deleteEvent(1L);

        verify(eventImageRepository).deleteByEventId(1L);
        verify(eventRepository).delete(testEvent);
    }

    @Test
    void testSetPublished_True() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        eventService.setPublished(1L, true);

        assertThat(testEvent.isPublished()).isTrue();
        verify(eventRepository).save(testEvent);
    }

    @Test
    void testUpdateEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse response = eventService.updateEvent(1L, eventRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(eventRequest.getTitle());
        verify(eventRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testDeleteEvent_NotFound() {
        setupMessageSource();
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.deleteEvent(2L))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void testSetPublished_EventNotFound() {
        setupMessageSource();
        when(eventRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.setPublished(3L, true))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void testCreateEvent_UserNotFound() {
        setupMessageSource();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(eventRequest, 99L))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void testCreateEvent_NullRequest() {
        setupMessageSource();
        assertThatThrownBy(() -> eventService.createEvent(null, 1L))
                .isInstanceOf(GlobalException.class); // Or NullPointerException, if not custom handled
    }

    @Test
    void testUpdateEvent_PartialUpdate() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventRequest partialRequest = new EventRequest();
        partialRequest.setTitle("Updated Title");

        EventResponse response = eventService.updateEvent(1L, partialRequest);

        assertThat(response.getTitle()).isEqualTo("Updated Title");
        verify(eventRepository).save(any(Event.class));
    }

}
