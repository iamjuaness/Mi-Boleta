package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.UpdateEventDTO;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-event")
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
    @PostMapping("/create-event")
    public ResponseEntity<MessageDTO<State>> createEvent(@RequestBody @Valid CreateEventDTO createEventDTO){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.createEvent(createEventDTO)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }

    /**
     * This endpoint is used to run the updateEvent service
     * @param updateEventDTO update info event
     * @param idEvent event's id
     * @return state action
     */
    @PutMapping("/update-event")
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
