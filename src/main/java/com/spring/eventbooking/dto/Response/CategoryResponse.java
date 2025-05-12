package com.spring.eventbooking.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}