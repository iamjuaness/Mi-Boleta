package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *
     * @param registerClientDTO
     * @return
     */
    @PostMapping("/signup-client")
    public ResponseEntity<User> signUpClient(@Valid @RequestBody RegisterClientDTO registerClientDTO){
        return ResponseEntity.ok().body(userService.signUp(registerClientDTO));
    }
}
