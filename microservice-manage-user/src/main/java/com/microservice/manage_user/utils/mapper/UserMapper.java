package com.microservice.manage_user.utils.mapper;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User dtoToEntity(RegisterClientDTO registerClientDTO, String passWordEncode){

        User user = new User();

        user.setIdUser(registerClientDTO.idUser());
        user.setName(registerClientDTO.name());
        user.setAddress(registerClientDTO.address());
        user.setPhoneNumber(registerClientDTO.phoneNumber());
        user.setEmailAddress(registerClientDTO.emailAddress());
        user.setPassword(passWordEncode);

        return user;
    }
}
