package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.ClientDTO;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.dto.UpdateUserDTO;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param loginClientDTO DTO with the information required for login
     * @return ResponseEntity<ClientDTO>
     */
    @GetMapping("/login-client")
    public ResponseEntity<ClientDTO> loginClient(@Valid @RequestBody LoginClientDTO loginClientDTO){
        return ResponseEntity.ok().body(userService.login(loginClientDTO));
    }

    /**
     * This endpoint is used to run the profileEdit service
     * @param id User's id
     * @param updateUserDTO DTO with the information required for Update
     * @throws ResourceNotFoundException
     */
    @PostMapping("/profile-edit/{id}")
    public void profileEdit(@PathVariable String id, @Valid @RequestBody UpdateUserDTO updateUserDTO) throws ResourceNotFoundException {
        userService.profileEdit(updateUserDTO, id);
    }
}
