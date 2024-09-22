package com.microservice.auth.presentation.controller;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.StateDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthServiceImpl authServiceImpl;

    @PostMapping(value ="/login-client",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageAuthDTO<TokenDTO>> loginClient(@Valid @RequestBody LoginClientDTO loginClientDTO) throws Exception {
        TokenDTO token =  authServiceImpl.loginClient(loginClientDTO);
        return ResponseEntity.ok().body(new MessageAuthDTO<TokenDTO>(false, token));
    }

    @PostMapping(value = "/register-client",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageAuthDTO<StateDTO>> registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO) throws Exception {
        StateDTO stateRegister = authServiceImpl.registerClient(registerClientDTO);

        return ResponseEntity.ok().body(new MessageAuthDTO<StateDTO>(false, stateRegister));
    }

    @PostMapping(value = "/activation-code")
    public  ResponseEntity<MessageAuthDTO<State>> activeAccount (@RequestParam("code") String code , @RequestParam("idUser") String idUser) throws Exception {
        State stateActiveAccount = authServiceImpl.activationAccount(code,idUser);
        return ResponseEntity.ok().body(new MessageAuthDTO<State>(false, stateActiveAccount));
    }
}
