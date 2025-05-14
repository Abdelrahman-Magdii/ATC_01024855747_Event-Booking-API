package com.spring.eventbooking.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private long totalUsers;
    private long totalEvents;
    private long totalBookings;
    private long publishedEvents;
    private long pendingBookings;
    private long confirmedBookings;
    private long cancelledBookings;
}
