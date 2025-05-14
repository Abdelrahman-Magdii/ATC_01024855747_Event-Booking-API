package com.spring.eventbooking.dto.Response;

import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.entity.Role;
import com.spring.eventbooking.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private Set<Long> roleIds;
    private List<Long> bookingIds;
    private List<Long> createdEventIds;

    public static UserResponse convertToDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));
        dto.setBookingIds(user.getBookings().stream().map(Booking::getId).collect(Collectors.toList()));
        dto.setCreatedEventIds(user.getCreatedEvents().stream().map(Event::getId).collect(Collectors.toList()));
        return dto;
    }

}
