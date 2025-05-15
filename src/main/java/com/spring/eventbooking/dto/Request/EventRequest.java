package com.spring.eventbooking.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "{event.title.required}")
    @Size(max = 255, message = "{event.title.size}")
    private String title;

    @Size(max = 2000, message = "{event.description.size}")
    private String description;

    @NotNull(message = "{event.startTime.required}")
    private LocalDateTime startTime;

    @NotNull(message = "{event.endTime.required}")
    private LocalDateTime endTime;

    @NotNull(message = "{event.capacity.required}")
    @Positive(message = "{event.capacity.positive}")
    private Integer capacity;

    @NotNull(message = "{event.price.required}")
    @Positive(message = "{event.price.positive}")
    private BigDecimal price;

    @NotBlank(message = "{event.location.required}")
    private String location;

    private boolean published = false;

    private List<String> categories;

    private List<String> tags;

    private MultipartFile image;
}