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
    void profileEdit(UpdateUserDTO updateUserDTO, String id) throws NotFoundException, ResourceNotFoundException;
    User getUser(String id) throws ResourceNotFoundException;
    List<User> getUsers() throws ResourceNotFoundException;

    void addToCart(AddToCartDTO addToCartDTO, String id) throws ErrorResponseException, ResourceNotFoundException;

    void deleteTicketsCart(String userId, String itemId) throws NotFoundException, ErrorResponseException, ResourceNotFoundException;
    void clearCart(String userId) throws ErrorResponseException, ResourceNotFoundException;
    void activateAccount(String id) throws ErrorResponseException, ResourceNotFoundException;
    void deleteAccount(String id) throws ResourceNotFoundException;
}
