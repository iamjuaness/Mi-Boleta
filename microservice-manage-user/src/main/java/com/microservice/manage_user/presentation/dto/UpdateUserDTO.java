package com.microservice.manage_user.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDTO(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "address is required")
        String address,
        @NotBlank(message = "phoneNumber is required")
        String phoneNumber,
        @NotBlank(message = "emailAddress is required")
        String emailAddress
) {
}
