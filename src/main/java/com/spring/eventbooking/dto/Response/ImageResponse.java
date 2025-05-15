package com.spring.eventbooking.dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageResponse {
    private Long id;
    private String url;
    private boolean isPrimary;
    private String altText;
}
