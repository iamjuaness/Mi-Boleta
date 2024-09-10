package com.microservice.manage_user.service.interfaces;

import com.microservice.manage_user.presentation.dto.AddToCartDTO;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.presentation.dto.UpdateUserDTO;
import com.microservice.manage_user.persistence.model.entities.User;
import jakarta.ws.rs.NotFoundException;
import org.springframework.web.ErrorResponseException;

import java.util.Optional;

public interface UserService {

    User signUp(RegisterClientDTO registerClientDTO) throws IllegalStateException;
    Optional<User> login(LoginClientDTO loginClientDTO) throws ErrorResponseException;
    User profileEdit(UpdateUserDTO updateUserDTO, String id) throws NotFoundException;
    void addToCart(AddToCartDTO addToCartDTO) throws ErrorResponseException;
    void deleteTicketsCart() throws NotFoundException, ErrorResponseException;
    void clearCart() throws ErrorResponseException;
    void activateAccount(String id) throws ErrorResponseException;
}
