package com.microservice.manage_user.service.interfaces;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import jakarta.ws.rs.NotFoundException;
import org.springframework.web.ErrorResponseException;

import java.util.List;

public interface UserService {

    State signUp(RegisterClientDTO registerClientDTO) throws IllegalStateException;
    ClientDTO login(LoginClientDTO loginClientDTO) throws ErrorResponseException;
    State profileEdit(UpdateUserDTO updateUserDTO, String id) throws NotFoundException, ResourceNotFoundException;
    User getUser(String id) throws ResourceNotFoundException;

    ClientDTO getUserByEmail(String email);

    List<User> getUsers() throws ResourceNotFoundException;

    void addToCart(AddToCartDTO addToCartDTO, String id) throws ErrorResponseException, ResourceNotFoundException;

    void deleteTicketsCart(String userId, String itemId) throws NotFoundException, ErrorResponseException, ResourceNotFoundException;
    void clearCart(String userId) throws ErrorResponseException, ResourceNotFoundException;
    State activateAccount(String id) throws ErrorResponseException, ResourceNotFoundException;
    State deleteAccount(String id) throws ResourceNotFoundException;

    State updateCode(String code, String id);

    State updatePassword(String password, String emailAddress);

    State validateCode(String code, String idUser);

    State deleteCode(String code, String id);
}
