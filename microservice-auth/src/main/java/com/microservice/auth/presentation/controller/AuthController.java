package com.microservice.auth.presentation.controller;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.*;
import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
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
    public ResponseEntity<MessageAuthDTO<StateDTO>> registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO)  {
        StateDTO stateRegister = authServiceImpl.registerClient(registerClientDTO);

        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, stateRegister));
    }

    @PostMapping(value = "/activation-code")
    public  ResponseEntity<MessageAuthDTO<State>> activeAccount (@RequestParam("code") String code , @RequestParam("idUser") String idUser)  {
        State stateActiveAccount = authServiceImpl.activationAccount(code,idUser);
        return ResponseEntity.ok().body(new MessageAuthDTO<>(false, stateActiveAccount));
    }

    @PostMapping(value = "/forgot-password")
    public  ResponseEntity<MessageDTO<State>> forgotPassword(@RequestParam("emailAddress") String emailAddress )  {
        State stateCodeForgotPsw = authServiceImpl.forgotPassword(emailAddress);
        if( stateCodeForgotPsw ==State.ERROR) {
            throw new IllegalArgumentException("El código de activación no se ha podido generar");
        }
        return ResponseEntity.ok().body(new MessageDTO<>(false, stateCodeForgotPsw));
    }

    @PostMapping(value = "/verify-code-forgot-password")
    public ResponseEntity<MessageDTO<State>> verifyCodeForgotPassword(@RequestParam("code") String code,@RequestParam("emailAddress") String emailAddress)  {
        State stateVerify = authServiceImpl.verifyForgotPassword(code,emailAddress);
        if( stateVerify ==State.ERROR) {
            throw  new IllegalArgumentException("no se ha autorizado la solicitud");
        }
        return ResponseEntity.ok().body(new MessageDTO<>(false, stateVerify));
    }
    @PatchMapping("/change-password")
    public ResponseEntity<MessageDTO<TokenDTO>> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        TokenDTO token = authServiceImpl.changePassword(changePasswordDTO);
        if(token ==null) {
            throw new IllegalArgumentException("No se ha autorizado la solicitud");
        }
        return ResponseEntity.ok().body(new MessageDTO<>(false,token));
    }
}
