package com.microservice.auth.presentation.advice;

public class ErrorResponseException extends RuntimeException {
    public ErrorResponseException(String message) {
        super(message);
    }

    public ErrorResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}