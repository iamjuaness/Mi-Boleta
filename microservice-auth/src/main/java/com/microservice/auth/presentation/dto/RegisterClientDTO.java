package com.microservice.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterClientDTO(

        @NotBlank(message = "id is required")
        String idUser,
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "address is required")
        String address,
        @NotBlank(message = "phoneNumber is required")
        String phoneNumber,
        @NotBlank(message = "emailAddress is required")
        String emailAddress,
        @NotBlank(message = "password is required")
        String password,
        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
}
