package com.microservice.auth.service.interfaces;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.StateDTO;
import com.microservice.auth.presentation.dto.TokenDTO;



public interface AuthService {

    TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception;
     StateDTO registerClient(RegisterClientDTO registerUserDto) throws Exception;
     TokenDTO loginMod(LoginClientDTO loginClientDTO ) throws Exception;
     State activationAccount (String code , String idUser)throws Exception;
}

