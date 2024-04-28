package com.dreamgames.backendengineeringcasestudy.model.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateUserRequest implements Serializable {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Email(message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\|,.<>?/]).+$"
            ,
            message = "Password must contain at least one lowercase, one uppercase, one digit, and one special character")
    private String password;

}
