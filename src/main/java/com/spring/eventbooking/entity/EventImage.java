package com.spring.eventbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "EVENT_IMAGE")
public class EventImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;

    private String url;
    private String altText;
    private boolean isPrimary;
    private LocalDateTime uploadedAt;

    public EventImage(long l, String url, String alt, boolean b) {
        this.id = l;
        this.url = url;
        this.altText = alt;
        this.isPrimary = b;
        this.uploadedAt = LocalDateTime.now();
    }
}

