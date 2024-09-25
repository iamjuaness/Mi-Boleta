package com.microservice.auth.presentation.controller;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.StateDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    final AuthServiceImpl authServiceImpl;
    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping(value ="/login-client",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageAuthDTO<TokenDTO>> loginClient(@Valid @RequestBody LoginClientDTO loginClientDTO) throws Exception {
        TokenDTO token =  authServiceImpl.loginClient(loginClientDTO);
        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, token));
    }

    @PostMapping(value = "/register-client",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageAuthDTO<StateDTO>> registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO) throws Exception {
        StateDTO stateRegister = authServiceImpl.registerClient(registerClientDTO);

        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, stateRegister));
    }

    @PostMapping(value = "/activation-code")
    public  ResponseEntity<MessageAuthDTO<State>> activeAccount (@RequestParam("code") String code , @RequestParam("idUser") String idUser) throws Exception {
        State stateActiveAccount = authServiceImpl.activationAccount(code,idUser);
        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, stateActiveAccount));
    }

    @PostMapping(value = "/forgot-password")
    public  ResponseEntity<MessageDTO<State>> forgotPassword(@RequestParam("emailAddress") String emailAddress ) throws Exception {
        State stateCodeForgotPsw = authServiceImpl.forgotPassword(emailAddress);
        if( stateCodeForgotPsw ==State.ERROR) {
            throw new IllegalArgumentException("El codigo de activación no se ha podido generar");
        }
        return ResponseEntity.ok().body(new MessageDTO<>(false, stateCodeForgotPsw));
    }
}
