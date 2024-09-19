package com.microservice.auth.presentation.controller;

import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthServiceImpl authServiceImpl;

    @PostMapping("/login-client")
    public ResponseEntity<MessageAuthDTO<TokenDTO>> loginClient(@Valid @RequestBody LoginClientDTO loginClientDTO) throws Exception {
        TokenDTO token =  authServiceImpl.loginClient(loginClientDTO);
        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, token));
    }

    @PostMapping("/register-client")
    public ResponseEntity<MessageAuthDTO> registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO) throws Exception {
        MessageAuthDTO messageAuthDTO = authServiceImpl.registerClient(registerClientDTO);
        System.out.println(messageAuthDTO);
        return ResponseEntity.ok().body(messageAuthDTO);
    }
}
