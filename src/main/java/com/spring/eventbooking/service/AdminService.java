package com.spring.eventbooking.service;

import com.spring.eventbooking.dto.Response.StatsResponse;
import com.spring.eventbooking.dto.Response.UserResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.entity.User;
import com.spring.eventbooking.enums.BookingStatus;
import com.spring.eventbooking.exception.GlobalException;
import com.spring.eventbooking.repository.BookingRepository;
import com.spring.eventbooking.repository.EventRepository;
import com.spring.eventbooking.repository.RoleRepository;
import com.spring.eventbooking.repository.UserRepository;
import com.spring.eventbooking.utiles.GlobalFunction;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserRole(Long userId, String roleName, String roleDescription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("user.not.found.id", userId), HttpStatus.NOT_FOUND));

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    newRole.setDescription(roleDescription);
                    return roleRepository.save(newRole);
                });

        if (roleDescription != null && !roleDescription.equals(role.getDescription())) {
            role.setDescription(roleDescription);
            roleRepository.save(role);
        }

        user.getRoles().clear();
        user.getRoles().add(role);

        userRepository.save(user);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public void setEventPublishStatus(Long eventId, boolean published) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GlobalException(GlobalFunction.getMS("event.not.found.id", eventId), HttpStatus.NOT_FOUND));

        event.setPublished(published);
        eventRepository.save(event);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public StatsResponse getSystemStats() {
        StatsResponse stats = new StatsResponse();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalEvents(eventRepository.count());
        stats.setTotalBookings(bookingRepository.count());
        stats.setPublishedEvents(eventRepository.countByPublishedTrue());
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.valueOf("PENDING")));
        stats.setConfirmedBookings(bookingRepository.countByStatus(BookingStatus.valueOf("CONFIRMED")));
        stats.setCancelledBookings(bookingRepository.countByStatus(BookingStatus.valueOf("CANCELLED")));

        return stats;
    }

}
