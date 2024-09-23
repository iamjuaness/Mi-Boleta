package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<MessageDTO<List<Event>>> getEvents(){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, eventService.getEvents()));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, null, e.getMessage()));
        }
    }
}
