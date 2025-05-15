package com.spring.eventbooking.dto.Response;

import com.spring.eventbooking.utiles.GlobalFunction;
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
        this(success, GlobalFunction.getMS(message), status, null);
    }
}
