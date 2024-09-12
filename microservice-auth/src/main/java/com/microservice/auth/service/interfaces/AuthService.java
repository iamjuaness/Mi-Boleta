package com.microservice.auth.service.interfaces;

import com.microservice.auth.persentation.dto.LoginClientDTO;
import com.microservice.auth.persentation.dto.RegisterClientDTO;
import com.microservice.auth.persentation.dto.TokenDTO;



public interface AuthService {

    TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception;
    TokenDTO registerClient(RegisterClientDTO registerUserDto) throws Exception;
    TokenDTO loginMod(LoginClientDTO loginClientDTO ) throws Exception;
}

