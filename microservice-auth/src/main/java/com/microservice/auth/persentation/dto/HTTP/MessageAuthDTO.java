package com.microservice.auth.persentation.dto.HTTP;

public record MessageAuthDTO <T> (
        boolean error,
        T response
) {
}
