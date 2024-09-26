package com.microservice.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordDTO(
        @NotBlank(message = "New password is required")
        @NotEmpty
        String newPassword,

        @NotBlank(message = "Email address is required")
        @Email
        @NotEmpty
        String emailAddress
) {
}
