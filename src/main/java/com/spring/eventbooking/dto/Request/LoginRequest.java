package com.spring.eventbooking.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "{login.email.required}")
    private String email;

    @NotBlank(message = "{login.password.required}")
    private String password;
}