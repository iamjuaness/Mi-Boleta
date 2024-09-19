package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manage-user")
public class ManageUserController {

    final UserRepository userRepository;
    final UserServiceImpl userService;

    public ManageUserController(UserRepository userRepository, UserServiceImpl userInterface) {
        this.userRepository = userRepository;
        this.userService = userInterface;
    }

    /**
     * This endpoint is used to run the signup service
     * @param registerClientDTO DTO with the information required for registration
     * @return ResponseEntity<State>
     */
    @PostMapping("/signup-client")
    public ResponseEntity<State> signUpClient(@Valid @RequestBody RegisterClientDTO registerClientDTO){
        return ResponseEntity.ok().body(userService.signUp(registerClientDTO));
    }

    /**
     * This endpoint is used to run the login service
     * @param emailAddress user's emailAddress
     * @param password user's password
     * @return ResponseEntity<ClientDTO>
     */
    @GetMapping("/login-client")
    public ResponseEntity<ClientDTO> loginClient(@Valid @RequestParam String emailAddress, @Valid @RequestParam String password){
        LoginClientDTO loginClientDTO = new LoginClientDTO(emailAddress, password);
        return ResponseEntity.ok().body(userService.login(loginClientDTO));
    }

    /**
     * This endpoint is used to run the profileEdit service
     * @param id User's id
     * @param updateUserDTO DTO with the information required for Update
     * @throws ResourceNotFoundException Resource not found
     */
    @PostMapping("/profile-edit/{id}")
    public ResponseEntity<Void> profileEdit(@PathVariable String id, @Valid @RequestBody UpdateUserDTO updateUserDTO) throws ResourceNotFoundException {
        userService.profileEdit(updateUserDTO, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This endpoint is used to run the getUser service
     * @param id User's id
     * @return User's information
     * @throws ResourceNotFoundException Resource not found
     */
    @GetMapping("/get-user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(userService.getUser(id));
    }

    /**
     * This endpoint is used to run the getUsers service
     * @return User's list information
     * @throws ResourceNotFoundException Resource not found
     */
    @GetMapping("/get-users")
    public ResponseEntity<List<User>> getUsers() throws ResourceNotFoundException {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    /**
     * This endpoint is used to run the addToCart service
     * @param addToCartDTO information for to add to cart
     * @param id user's id
     * @throws ResourceNotFoundException if is cannot to add to cart
     */
    @PutMapping("/add-to-cart/{id}")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartDTO addToCartDTO, @PathVariable String id) throws ResourceNotFoundException {
        userService.addToCart(addToCartDTO, id);
        return ResponseEntity.noContent().build();
    }


    /**
     * This endpoint is used to run the deleteTicketsCart
     * @param userId user's id
     * @param itemId item's id
     * @throws ResourceNotFoundException if is cannot to add to cart
     */
    @PutMapping("/delete-tickets-cart/{userId}/cart/{itemId}")
    public ResponseEntity<Void> deleteTicketsCart(@PathVariable String userId, @PathVariable String itemId) throws ResourceNotFoundException {
        userService.deleteTicketsCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * This endpoint is used to run the clearCart service
     * @param userId user's id
     * @throws ResourceNotFoundException if is cannot to clear the cart
     */
    @PutMapping("/clear-cart/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) throws ResourceNotFoundException {
        userService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * This endpoint is used to run the activateAccount service
     * @param id validation code
     * @throws ResourceNotFoundException if is cannot to activate the account
     */
    @PutMapping("/activate-account/{id}")
    public ResponseEntity<Void> activateAccount(@PathVariable String id) throws ResourceNotFoundException {
        userService.activateAccount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * This endpoint is used to run the deleteAccount service
     * @param id User's id
     */
    @PutMapping("/delete-account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id) throws ResourceNotFoundException {
        userService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
