package com.microservice.manage_user.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientDTO(
        @NotBlank(message = "IdUser is required")
        String idUser,
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Role is required")
        String role,
        @NotBlank(message = "email is required")
        String email
) {
}