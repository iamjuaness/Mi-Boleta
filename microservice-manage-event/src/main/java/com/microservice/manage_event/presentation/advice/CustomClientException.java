package com.microservice.manage_event.presentation.advice;

public class CustomClientException extends RuntimeException {
    public CustomClientException(String message) {
        super(message);
    }
}
