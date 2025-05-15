package com.spring.eventbooking.controller;

import com.spring.eventbooking.dto.Request.PublishedRequest;
import com.spring.eventbooking.dto.Request.RoleUpdateRequest;
import com.spring.eventbooking.dto.Response.ApiResponse;
import com.spring.eventbooking.dto.Response.StatsResponse;
import com.spring.eventbooking.dto.Response.UserResponse;
import com.spring.eventbooking.entity.Booking;
import com.spring.eventbooking.entity.Event;
import com.spring.eventbooking.service.AdminService;
import com.spring.eventbooking.utiles.GlobalFunction;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Controller")
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
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest roleUpdate) {
        adminService.updateUserRole(id, roleUpdate.getName(), roleUpdate.getDescription());
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        GlobalFunction.getMS("user.role.updated")
                        , HttpStatus.OK.value()
                ));
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(adminService.getAllEvents());
    }

    @PutMapping("/events/{id}/publish")
    public ResponseEntity<ApiResponse> publishEvent(
            @PathVariable Long id,
            @RequestBody PublishedRequest publishDto) {
        adminService.setEventPublishStatus(id, publishDto.isPublished());
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        publishDto.isPublished() ? GlobalFunction.getMS("event.publish.success") : GlobalFunction.getMS("event.unpublish.success"),
                        HttpStatus.OK.value()));
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