package com.spring.eventbooking.dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> categories;
    private List<String> tags;
    private List<ImageResponse> images;

}