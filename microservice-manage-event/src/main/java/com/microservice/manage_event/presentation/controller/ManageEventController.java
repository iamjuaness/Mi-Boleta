package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.UpdateEventDTO;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping("/api/manage-event")
@Tag(name = "Manage Event", description = "Private controller requiring authentication to access your endpoints")
public class ManageEventController {

    final EventServiceImpl eventService;

    public ManageEventController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    /**
     * This endpoint is used to run the createEvent service
     * @param createEventDTO event info
     * @return state action
     */
    @PostMapping(value = "/create-event", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create Event",
            description = "Create a Event in the platform",
            tags = {"CRUD"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CreateEventDTO contain the information for create a event",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    implementation = CreateEventDTO.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful Create Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Create Event Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> createEvent(@ModelAttribute @Valid CreateEventDTO createEventDTO){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.createEvent(createEventDTO)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, State.ERROR, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the updateEvent service
     * @param updateEventDTO update info event
     * @param idEvent event's id
     * @return state action
     */
    @PutMapping("/update-event")
    @Operation(
            summary = "Update Event",
            description = "Update a Event that exists in the platform",
            tags = {"CRUD"},
            parameters = {
                    @Parameter(
                            name = "idEvent",
                            description = "Event's id",
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "UpdateEventDTO contain the information for update a event",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UpdateEventDTO.class
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Update Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Update Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> updateEvent(@RequestBody @Valid UpdateEventDTO updateEventDTO, @RequestParam String idEvent){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.updateEvent(updateEventDTO, idEvent)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the deleteEvent service
     * @param idEvent event's id
     * @return state action
     */
    @PutMapping("/delete-event")
    @Operation(
            summary = "Delete Event",
            description = "Delete a Event in the platform",
            tags = {"CRUD"},
            parameters = {
                    @Parameter(
                            name = "idEvent",
                            description = "Event's id",
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
                            description = "Successful - Delete Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Delete Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<MessageDTO<State>> deleteEvent(@RequestParam String idEvent){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.deleteEvent(idEvent)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }
}
