package com.microservice.manage_user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserDTO(
        @NotBlank(message = "name is required")
        @NotEmpty
        String name,
        @NotBlank(message = "address is required")
        @NotEmpty
        String address,
        @NotBlank(message = "phoneNumber is required")
        @NotEmpty
        String phoneNumber,
        @NotBlank(message = "emailAddress is required")
        @Email
        @NotEmpty
        String emailAddress,
        @NotEmpty
        String password
) {
}
