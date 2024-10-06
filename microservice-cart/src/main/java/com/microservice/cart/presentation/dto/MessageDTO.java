package com.microservice.cart.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MessageDTO<T> {
    private boolean error;
    private T data;
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
