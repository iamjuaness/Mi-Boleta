package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.advice.CustomClientException;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.ClientDTO;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.StateDTO;
import com.microservice.manage_user.presentation.dto.http.MessageDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-user")
public class ManageUserNonAuthorizationController {

    final UserRepository userRepository;
    final UserServiceImpl userService;

    public ManageUserNonAuthorizationController(UserRepository userRepository, UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * This endpoint is used to run the signup service
     * @param registerClientDTO DTO with the information required for registration
     * @return ResponseEntity<State>
     */
    @PostMapping(value = "/signup-client", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO<StateDTO>> signUpClient(@Valid @RequestBody RegisterClientDTO registerClientDTO){
        try {
            State state = userService.signUp(registerClientDTO);

            return ResponseEntity.ok().body(new MessageDTO<>(false, new StateDTO(state, State.INACTIVE, registerClientDTO.idUser())));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new StateDTO(State.ERROR, null, null), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, new StateDTO(State.ERROR, null, null) , e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the login service
     * @param emailAddress user's emailAddress
     * @param password user's password
     * @return ResponseEntity<ClientDTO>
     */
    @GetMapping(value ="/login-client",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO<ClientDTO>> loginClient(@Valid @RequestParam String emailAddress, @Valid @RequestParam String password){
        try {
            LoginClientDTO loginClientDTO = new LoginClientDTO(emailAddress, password);
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.login(loginClientDTO)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  ""), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  ""), e.getMessage()));
        }
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
     */
    @GetMapping("/get-users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    /**
     * This endpoint is used to run the activateAccount service
     * @param id validation code
     */
    @PutMapping("/activate-account/{id}")
    public ResponseEntity<MessageDTO<State>> activateAccount(@PathVariable String id) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.activateAccount(id)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the saveCodeValidation service
     * @param code validation code
     * @param id user's id
     * @return save codeValidation state
     */
    @PutMapping("/save-code-validation")
    public ResponseEntity<MessageDTO<State>> saveCodeValidation(@RequestParam String code, @RequestParam String id){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.updateCode(code, id)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run validateCode service
     * @param code validation code
     * @param idUser user's id
     * @return validation state
     */
    @PutMapping("/validate-code")
    public ResponseEntity<MessageDTO<State>> validateCode(@RequestParam String code, @RequestParam String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.validateCode(code, idUser)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

}
