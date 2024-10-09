package com.microservice.manage_user.presentation.controller;

import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.CustomClientException;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.dto.http.MessageDTO;
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


@RestController
@RequestMapping("/api/manage-user")
@Tag(name = "Manage User", description = "Private controller requiring authentication to access your endpoints")
public class ManageUserController {

    final UserRepository userRepository;
    final UserServiceImpl userService;

    public ManageUserController(UserRepository userRepository, UserServiceImpl userInterface) {
        this.userRepository = userRepository;
        this.userService = userInterface;
    }

    /**
     * This endpoint is used to run the profileEdit service
     * @param idUser User's id
     * @param updateUserDTO DTO with the information required for Update
     * @throws ResourceNotFoundException Resource not found
     */
    @PostMapping("/profile-edit/{idUser}")
    @Operation(
            summary = "Edit user's profile",
            description = "This endpoint is used to edit basic user information.",
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The UpdateUserDTO contains the name, address, phone number, email address.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UpdateUserDTO.class
                            )
                    )
            ),
            tags = {"Profile"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful edit",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> profileEdit(@PathVariable String idUser, @Valid @RequestBody UpdateUserDTO updateUserDTO) throws ResourceNotFoundException {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.profileEdit(updateUserDTO, idUser)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }


    /**
     * This endpoint is used to run the deleteAccount service
     * @param idUser User's id
     */
    @PutMapping("/delete-account/{idUser}")
    @Operation(
            summary = "Delete account",
            description = "This endpoint is used to delete a user's account.",
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
            tags = {"Account"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful delete",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = State.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> deleteAccount(@PathVariable String idUser) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.deleteAccount(idUser)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @PutMapping(value = "/add-cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO<State>> addToCart(@RequestBody AddToCartDTO addToCartDTO, String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.addToCart(addToCartDTO, idUser)));
        } catch (CustomClientException | ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/delete-item-cart")
    public ResponseEntity<MessageDTO<State>> deleteItemCart(@RequestParam String idUser, @RequestParam String idEventVO){
        try {
            System.out.println("Llego al cliente" + idUser + idEventVO);
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.deleteTicketsCart(idUser, idEventVO)));
        } catch (CustomClientException | ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/clear-cart")
    public ResponseEntity<MessageDTO<State>> clearCart(String idUser){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, userService.clearCart(idUser)));
        } catch (CustomClientException | ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }
}
