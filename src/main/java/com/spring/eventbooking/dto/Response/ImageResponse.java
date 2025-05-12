package com.spring.eventbooking.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ImageResponse {
    private Long id;
    private String imageUrl;
    private String altText;
}
