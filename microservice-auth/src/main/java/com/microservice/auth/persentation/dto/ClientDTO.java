package com.microservice.auth.persentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientDTO(
        @NotBlank(message = "Id user is required")
        String idUser,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "role is required")
        String role,
        @NotBlank(message = "email is required")
        String emailAddress
) {
}
