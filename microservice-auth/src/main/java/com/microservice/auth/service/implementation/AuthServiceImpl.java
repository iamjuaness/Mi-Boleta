package com.microservice.auth.service.implementation;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.persentation.dto.ClientDTO;
import com.microservice.auth.persentation.dto.LoginClientDTO;
import com.microservice.auth.persentation.dto.RegisterClientDTO;
import com.microservice.auth.persentation.dto.TokenDTO;
import com.microservice.auth.service.interfaces.AuthService;
import com.microservice.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.AttributeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtils jwtUtilsService;

    @Autowired
    private ManageUserClient manageUserClient;

    private PasswordEncoder passwordEncoder;

    @Override
    public TokenDTO loginClient(LoginClientDTO loginClientDTO) throws Exception {

        try {
            //verificar si tiene datos
            if (loginClientDTO == null) {
                throw new Exception("Por favor ingrese un valor valido");
            }
            String passwordHash = passwordEncoder.encode(loginClientDTO.password());

            //Buscar el usuario en el msvc UserManage
            ClientDTO user = manageUserClient.getClient(loginClientDTO.emailAddress(), passwordHash) ;

            //Verificar que exista ese usuario
            if (user == null) {
                throw new Exception("No se ha encontrado un usuario asocioado a la información proporcionada");
            }

            // Crear los atributos del token de autenticación
            Map<String, Object> authToken = new HashMap<>();
            authToken.put("role", "USER");
            authToken.put("nombre", user.name());
            authToken.put("id", user.idUser());

            //generar el token
            String token = jwtUtilsService.generarToken(user.emailAddress(), authToken);

            // Crear un objeto TokenDto para devolver el token generado
            return new TokenDTO(token);

        } catch (Exception e) {
            throw new Exception("some went wrong");

        }
    }

    @Override
    public TokenDTO registerClient(RegisterClientDTO registerUserDto)throws Exception {

        //verificar que que el DTO no sea nulo
        Objects.requireNonNull(registerUserDto, "Por favor ingrese información ");


    }

    @Override
    public TokenDTO loginMod(LoginClientDTO loginClientDTO) throws Exception {
        return null;
    }
}
