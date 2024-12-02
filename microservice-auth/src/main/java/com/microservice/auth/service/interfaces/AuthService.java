package com.microservice.auth.service.interfaces;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.*;


public interface AuthService {

    TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception;
     StateDTO registerClient(RegisterClientDTO registerUserDto) throws Exception;
     TokenDTO loginMod(LoginClientDTO loginClientDTO ) throws Exception;
     State activationAccount (String code , String idUser)throws Exception;
     State forgotPassword (String emailAddress)throws Exception;
     State verifyForgotPassword (String code, String emailAddress)throws Exception;
     TokenDTO changePassword (ChangePasswordDTO changePasswordDTO)throws Exception;
     State verifyToken(String token)throws Exception;
}

