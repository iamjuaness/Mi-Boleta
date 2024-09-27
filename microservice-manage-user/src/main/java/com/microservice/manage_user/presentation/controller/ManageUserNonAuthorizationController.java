package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.advice.CustomClientException;
import com.microservice.manage_user.presentation.dto.ClientDTO;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.StateDTO;
import com.microservice.manage_user.presentation.dto.http.MessageDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/request-user")
@Tag(name = "Request User", description = "This controller receives all public requests made to the manage-user microservice.")
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
    @Operation(
            summary = "Sign-Up Client",
            description = "Saves a user in the database by encrypting the password",
            tags = {"Account"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Contains the user information to be stored in the database.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = RegisterClientDTO.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful sign-up",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StateDTO.class
                                    )
                            )
                    )
            }
    )
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
    @Operation(
            summary = "Login Client",
            description = "Obtains user information with respective credentials",
            tags = {"Account"},
            parameters = {
                    @Parameter(
                            name = "emailAddress",
                            description = "User's email address",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "password",
                            description = "User's password",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Login",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ClientDTO.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<ClientDTO>> loginClient(@Valid @RequestParam String emailAddress, @Valid @RequestParam String password){
        try {
            LoginClientDTO loginClientDTO = new LoginClientDTO(emailAddress, password);
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.login(loginClientDTO)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  "", null), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  "", null), e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the getUser service
     * @param idUser User's id
     * @return User's information
     */
    @GetMapping("/get-user/{idUser}")
    @Operation(
            summary = "Get User",
            description = "Gets a user by its id",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "idUser",
                            description = "User's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Get User",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = User.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<User>> getUser(@PathVariable String idUser) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.getUser(idUser)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new User(), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, new User(), e.getMessage()));
        }
    }

    @GetMapping("/get-user-byEmail")
    @Operation(
            summary = "Get User By Email",
            description = "Gets a user by its email",
            tags = {"User"},
            parameters = {
                    @Parameter(
                            name = "emailAddress",
                            description = "User's emailAddress",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Get User By Email",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ClientDTO.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<ClientDTO>> getUserByEmail(@RequestParam String emailAddress){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.getUserByEmail(emailAddress)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  "", null), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, new ClientDTO("",
                    "", null,  "", null), e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the getUsers service
     * @return User's list information
     */
    @GetMapping("/get-users")
    @Operation(
            summary = "Get Users",
            description = "Gets all users who are active on the platform.",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Get Users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  List.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<List<User>>> getUsers() {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.getUsers()));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the getAllUsers service
     * @return User's list information
     */
    @GetMapping("/get-all-users")
    @Operation(
            summary = "Get All Users",
            description = "Gets all users who are active or inactive on the platform.",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Get Users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  List.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<List<User>>> getAllUsers() {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.getAllUsers()));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        }
    }


    /**
     * This endpoint is used to run the activateAccount service
     * @param idUser validation code
     */
    @PutMapping("/activate-account/{idUser}")
    @Operation(
            summary = "Activate Account",
            description = "Activate the user's account",
            tags = {"Account"},
            parameters = {
                    @Parameter(
                            name = "idUser",
                            description = "User's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Activate Account",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> activateAccount(@PathVariable String idUser) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.activateAccount(idUser)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the saveCodeValidation service
     * @param code validation code
     * @param idUser user's id
     * @return save codeValidation state
     */
    @PutMapping("/save-code-validation")
    @Operation(
            summary = "Save code validation",
            description = "What this does is to store a code in a directory inside the database.",
            tags = {"Account"},
            parameters = {
                    @Parameter(
                            name = "code",
                            description = "User's code",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "idUser",
                            description = "User's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )

            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Activate Account",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> saveCodeValidation(@RequestParam String code, @RequestParam String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.updateCode(code, idUser)));
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
    @Operation(
            summary = "Validate code",
            description = "What this does is to validate that the user has a saved code equal to the one I am sending.",
            tags = {"Account"},
            parameters = {
                    @Parameter(
                            name = "code",
                            description = "User's code",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "idUser",
                            description = "User's id",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )

            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Activate Account",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> validateCode(@RequestParam String code, @RequestParam String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.validateCode(code, idUser)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run updatePassword service
     * @param password New User's password
     * @param emailAddress User's emailAddress
     * @return state action
     */
    @PutMapping("/update-password")
    @Operation(
            summary = "Update password",
            description = "Update the user's password",
            tags = {"Account"},
            parameters = {
                    @Parameter(
                            name = "password",
                            description = "New user's password",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "emailAddress",
                            description = "User's emailAddress",
                            required = true,
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )

            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Activate Account",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation =  State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> updatePassword(@RequestParam String password, @RequestParam String emailAddress){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.updatePassword(password, emailAddress)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }
}
