package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.GlobalEventStatsDTO;
import com.microservice.manage_event.presentation.dto.ListEventStatsDTO;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public List<Event> filterEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer capacity) {

        return eventService.filterEvents(name, startDate, endDate, address, capacity != null ? capacity : 0);
    }

    /**
     * This endpoint is used to run getEventStats
     * @return stats global list
     */
    @GetMapping("/general-stats")
    public List<GlobalEventStatsDTO> getEventStats() {
        return eventService.getEventStatistics();
    }

    /**
     * This endpoint is used to run getEventStatsByEvent
     * @return stats by event list
     */
    @GetMapping("/event-stats")
    public List<ListEventStatsDTO> getEventStatsByEvent() {
        return eventService.getStatisticsByEvent();
    }
}
