package com.microservice.auth.service.interfaces;

import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.TokenDTO;



public interface AuthService {

    TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception;
     MessageAuthDTO registerClient(RegisterClientDTO registerUserDto) throws Exception;
     TokenDTO loginMod(LoginClientDTO loginClientDTO ) throws Exception;
}

