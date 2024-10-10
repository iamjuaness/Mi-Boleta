package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.GlobalEventStatsDTO;
import com.microservice.manage_event.presentation.dto.ListEventStatsDTO;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/public-event")
public class ManageEventPublicController {

    final EventServiceImpl eventService;

    public ManageEventPublicController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    /**
     * This endpoint is used to run the getEvent service
     * @param idEvent event's id
     * @return event
     */
    @GetMapping("/get-event/{idEvent}")
    @Operation(
            summary = "Get an event by id",
            description = "Get an event at database by its id",
            tags = {"Event"},
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
                            description = "Successful - Get Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Get Event",
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
    public ResponseEntity<MessageDTO<Event>> getEvent(@PathVariable String idEvent){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.getEvent(idEvent)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the getEvents service
     * @return event list
     */
    @GetMapping("/get-events")
    @Operation(
            summary = "Get all events",
            description = "Get all database's events",
            tags = {"Event"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Get Events",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Get Events",
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
    public ResponseEntity<MessageDTO<List<Event>>> getEvents(){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.getEvents()));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the filterEvents
     * @param name (optional) event's name
     * @param startDate (optional) event's startDate
     * @param endDate (optional) event's endDate
     * @param address (optional) event's address
     * @param capacity (optional) event's capacity
     * @return event's list filtered
     */
    @GetMapping("/filter")
    @Operation(
            summary = "Filter Events",
            description = "Filter an event by name, startDate, endDate, address and capacity",
            tags = {"Event"},
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Event's name",
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "startDate",
                            description = "Event's startDate",
                            content = @Content(
                                    mediaType = "ISOString",
                                    schema = @Schema(
                                            implementation = LocalDate.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "endDate",
                            description = "Event's endDate",
                            content = @Content(
                                    mediaType = "ISOString",
                                    schema = @Schema(
                                            implementation = LocalDate.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "address",
                            description = "Event's address",
                            content = @Content(
                                    mediaType = "String",
                                    schema = @Schema(
                                            implementation = String.class
                                    )
                            )
                    ),
                    @Parameter(
                            name = "capacity",
                            description = "Event's capacity",
                            content = @Content(
                                    mediaType = "Integer",
                                    schema = @Schema(
                                            implementation = Integer.class
                                    )
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Filter Events",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Filter Events",
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
    public ResponseEntity<MessageDTO<List<Event>>> filterEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer capacity) {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.filterEvents(name, startDate, endDate, address, capacity != null ? capacity : 0)));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList()));
        }
    }

    /**
     * This endpoint is used to run getEventStats
     * @return stats global list
     */
    @GetMapping("/general-stats")
    @Operation(
            summary = "General stats Events",
            description = "General stats events how name, totalTicketsSold, totalCapacity, totalAvailableTickets, totalEvents",
            tags = {"Stats"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - General stats Events",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - General stats Events",
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
    public ResponseEntity<MessageDTO<List<GlobalEventStatsDTO>>> getEventStats() {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.getEventStatistics()));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList()));
        }
    }

    /**
     * This endpoint is used to run getEventStatsByEvent
     * @return stats by event list
     */
    @GetMapping("/event-stats")
    @Operation(
            summary = "Stats by Events",
            description = "Stats by event how name, totalTicketsSold, totalCapacity, totalAvailableTickets, totalEvents",
            tags = {"Stats"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Stats by Event",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MessageDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "417",
                            description = "Error - Stats by Event",
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
    public ResponseEntity<MessageDTO<List<ListEventStatsDTO>>> getEventStatsByEvent() {
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.getStatisticsByEvent()));
        } catch (CustomClientException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList()));
        }
    }
}
