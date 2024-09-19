package com.microservice.auth.service.implementation;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.advice.ResourceNotFoundException;
import com.microservice.auth.presentation.dto.ClientDTO;
import com.microservice.auth.presentation.dto.HTTP.MessageAuthDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.interfaces.AuthService;
import com.microservice.auth.utils.ActivationCodeGenerator;
import com.microservice.auth.utils.JwtUtils;
import com.microservice.auth.utils.ValidatePassword;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    final JwtUtils jwtUtilsService;
    final ManageUserClient manageUserClient;
    final PasswordEncoder passwordEncoder;
    final ValidatePassword validatePassword;
    final ActivationCodeGenerator activationCodeGenerator;
    final MailServiceImpl mailService;


    public AuthServiceImpl(JwtUtils jwtUtilsService, ManageUserClient manageUserClient, PasswordEncoder passwordEncoder, ValidatePassword validatePassword, ActivationCodeGenerator activationCodeGenerator, MailServiceImpl mailService) {
        this.jwtUtilsService = jwtUtilsService;
        this.manageUserClient = manageUserClient;
        this.passwordEncoder = passwordEncoder;
        this.validatePassword = validatePassword;
        this.activationCodeGenerator = activationCodeGenerator;
        this.mailService = mailService;
    }

    @Override
    public TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception {

        try {
            //verificar si tiene datos
            if (loginClientDTO == null) {
                throw new IllegalArgumentException("Por favor ingrese un valor valido");
            }

            //Buscar el usuario en el msvc UserManage
            ClientDTO user = manageUserClient.getClient(loginClientDTO.emailAddress(), loginClientDTO.password()) ;

            //Verificar que exista ese usuario
            if (user == null) {
                throw new ResourceNotFoundException("No se ha encontrado un usuario asocioado a la información proporcionada");
            }

            // Crear los atributos del token de autenticación
            Map<String, Object> authToken = new HashMap<>();
            authToken.put("role", user.role());
            authToken.put("name", user.name());
            authToken.put("idUser", user.idUser());

            //generar el token
                String token = jwtUtilsService.generarToken(user.emailAddress(), authToken);

                // Crear un objeto TokenDto para devolver el token generado
                return new TokenDTO(token);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageAuthDTO registerClient(RegisterClientDTO registerUserDto)throws Exception {

        try {
            //Verificar que no esté vacio el formulario de registro
            if (registerUserDto == null) {
                throw new IllegalArgumentException("Por favor ingrese un valor valido");
            }
            //verificar que sea válida la contraseña con los parametros de seguiridad
            if (validatePassword.validarContrasena(registerUserDto.password()) == false) {
                System.out.println();
                throw new IllegalArgumentException("La contraseña no cumple con los parámetros especificados");
            }
            //obtener el estado del registro del mscv manageUser
            State stateRegister = manageUserClient.registerClient(registerUserDto);
            System.out.println(stateRegister);

            if (stateRegister != State.SUCCESS) {
                throw new Exception("El estado del registro no es satisfactorio");
            }

            //generar código de verificación
            String codeActivation = activationCodeGenerator.generateActivationCode();
            System.out.println(codeActivation);

            //Enviar email con código
            mailService.sendMail(registerUserDto.emailAddress(),"Código de autentificación","Bienvenido a Mi boleta, este es su código de verificación de cuenta: " +codeActivation);

             MessageAuthDTO response = new MessageAuthDTO(false,"Se ha registrado satisfactoriamente, se ha enviado un código de activación a la dirección de correo:" + registerUserDto.emailAddress());

             return response;

        }catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        MessageAuthDTO response = new MessageAuthDTO(true,"some went wrong");

        return response;
    }

    @Override
    public TokenDTO loginMod(LoginClientDTO loginClientDTO) throws Exception {
        return null;
    }
}
