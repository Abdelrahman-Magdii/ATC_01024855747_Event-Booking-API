package com.spring.eventbooking.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private int status;
    private Object data;

    public ApiResponse(boolean success, String message, int status) {
        this(success, message, status, null);
    }
}
