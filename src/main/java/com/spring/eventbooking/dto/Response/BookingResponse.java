package com.spring.eventbooking.dto.Response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.eventbooking.enums.BookingStatus;
import com.spring.eventbooking.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long eventId;
    private String eventTitle;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime eventStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime eventEndTime;

    private String eventLocation;
    private Integer numberOfTickets;
    private BigDecimal totalPrice;
    private BookingStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime bookedAt;

    private PaymentStatus paymentStatus;
    private String paymentReference;
}