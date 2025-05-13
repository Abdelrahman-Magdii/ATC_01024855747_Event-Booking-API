package com.spring.eventbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "BOOKING")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;

    private Integer numberOfTickets;
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status; // CONFIRMED, CANCELLED, PENDING

    private LocalDateTime bookedAt;
    private String paymentStatus;
    private String paymentReference;

    // Getters, setters, constructors
}

