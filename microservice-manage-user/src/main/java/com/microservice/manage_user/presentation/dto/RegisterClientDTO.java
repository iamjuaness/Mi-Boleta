package com.microservice.manage_user.presentation.dto;

import com.microservice.manage_user.persistence.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterClientDTO(

        @NotBlank(message = "id is required")
        String idUser,
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "address is required")
        String address,
        @NotNull(message = "role is required")
        Role role,
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
