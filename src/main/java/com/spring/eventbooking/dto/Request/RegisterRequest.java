package com.spring.eventbooking.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
//            message = "{validation.password.pattern}")
    private String password;

    @NotBlank(message = "{validation.firstname.required}")
    private String firstName;

    @NotBlank(message = "{validation.lastname.required}")
    private String lastName;

    @NotBlank(message = "{validation.phonenumber.required}")
    @Pattern(regexp = "^\\+[0-9]{10,15}$", message = "{validation.phonenumber.pattern}")
    @Size(max = 15, message = "{validation.phonenumber.max}")
    private String phoneNumber;

    public RegisterRequest(String mail, String first, String last, String password) {
        this.email = mail;
        this.firstName = first;
        this.lastName = last;
        this.password = password;
    }
}