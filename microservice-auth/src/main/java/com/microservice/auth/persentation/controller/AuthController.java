package com.microservice.auth.persentation.controller;

import com.microservice.auth.persentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.persentation.dto.LoginClientDTO;
import com.microservice.auth.persentation.dto.RegisterClientDTO;
import com.microservice.auth.persentation.dto.TokenDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;
import com.microservice.auth.service.interfaces.AuthService;
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
}
