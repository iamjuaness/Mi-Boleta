package com.microservice.auth.service.implementation;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.presentation.advice.ResourceNotFoundException;
import com.microservice.auth.presentation.dto.ClientDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.interfaces.AuthService;
import com.microservice.auth.utils.JwtUtils;
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

    public AuthServiceImpl(JwtUtils jwtUtilsService, ManageUserClient manageUserClient, PasswordEncoder passwordEncoder) {
        this.jwtUtilsService = jwtUtilsService;
        this.manageUserClient = manageUserClient;
        this.passwordEncoder = passwordEncoder;
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
    public TokenDTO registerClient(RegisterClientDTO registerUserDto)throws Exception {

        //verificar que que el DTO no sea nulo
        Objects.requireNonNull(registerUserDto, "Por favor ingrese información ");

        return null;

    }

    @Override
    public TokenDTO loginMod(LoginClientDTO loginClientDTO) throws Exception {
        return null;
    }
}
