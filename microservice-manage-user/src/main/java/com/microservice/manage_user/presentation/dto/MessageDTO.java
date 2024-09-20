package com.microservice.manage_user.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDTO<T> {
    private final boolean error;
    private final T data;
    private String message;  // Optional field for error messages
    private String errorCode; //Optional field for error codes

    // Builder for successful cases
    public MessageDTO(boolean error, T data) {
        this.error = error;
        this.data = data;
    }

    // Builder for error cases, including message and code
    public MessageDTO(boolean error, T data, String message, String errorCode) {
        this.error = error;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
    }

    public MessageDTO(boolean error, T data, String message){
        this.error = error;
        this.data = data;
        this.message = message;
    }
}
