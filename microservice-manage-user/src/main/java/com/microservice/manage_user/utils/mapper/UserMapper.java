package com.microservice.manage_user.utils.mapper;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.Role;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.dto.ClientDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.presentation.dto.UpdateUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User dtoRegisterToEntity(RegisterClientDTO registerClientDTO, String passWordEncode){

        User user = new User();

        user.setIdUser(registerClientDTO.idUser());
        user.setRole(Role.CLIENT);
        user.setState(State.INACTIVE);
        user.setName(registerClientDTO.name());
        user.setAddress(registerClientDTO.address());
        user.setPhoneNumber(registerClientDTO.phoneNumber());
        user.setEmailAddress(registerClientDTO.emailAddress());
        user.setPassword(passWordEncode);

        return user;
    }

    public User dtoUpdateProfileToEntity(UpdateUserDTO updateUserDTO, User user){

        user.setName(updateUserDTO.name());
        user.setAddress(updateUserDTO.address());
        user.setPhoneNumber(updateUserDTO.phoneNumber());
        user.setEmailAddress(updateUserDTO.emailAddress());

        return user;
    }

    public ClientDTO entityToClientDTO(User user){
        if (user == null){
            throw new IllegalArgumentException("User null");
        }
        return new ClientDTO(user.getIdUser(), user.getName(), user.getRole(), user.getEmailAddress(), user.getState());
    }
}
