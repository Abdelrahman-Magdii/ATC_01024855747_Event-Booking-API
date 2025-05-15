package com.spring.eventbooking.dto.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse {
    private Date timestamp;
    private String message;
    private String details;
    private Map<String, List<String>> errors;

    public ValidationErrorResponse(Date timestamp, String message, String details, Map<String, List<String>> errors) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.errors = errors;
    }
}
