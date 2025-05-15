package com.spring.eventbooking.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    @NotNull(message = "{booking.eventId.required}")
    private Long eventId;

    @NotNull(message = "{booking.tickets.required}")
    @Positive(message = "{booking.tickets.positive}")
    private Integer numberOfTickets = 1;
}