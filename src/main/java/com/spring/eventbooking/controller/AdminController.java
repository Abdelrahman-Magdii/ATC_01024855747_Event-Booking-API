package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.PublishedRequest;
import com.spring.eventbooking.dto.Request.RoleUpdateRequest;
import com.spring.eventbooking.dto.Response.StatsResponse;
import com.spring.eventbooking.dto.Response.UserResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.service.AdminService;
import com.spring.eventbooking.utiles.GlobalFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest roleUpdate) {
        adminService.updateUserRole(id, roleUpdate.getName(), roleUpdate.getDescription());
        return ResponseEntity.ok(Map.of("message", GlobalFunction.getMS("user.role.updated"), "status", "OK"));
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(adminService.getAllEvents());
    }

    @PutMapping("/events/{id}/publish")
    public ResponseEntity<Map<String, String>> publishEvent(
            @PathVariable Long id,
            @RequestBody PublishedRequest publishDto) {
        adminService.setEventPublishStatus(id, publishDto.isPublished());
        return ResponseEntity.ok(Map.of("message",
                publishDto.isPublished() ? GlobalFunction.getMS("event.publish.success") : GlobalFunction.getMS("event.unpublish.success")));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }
}