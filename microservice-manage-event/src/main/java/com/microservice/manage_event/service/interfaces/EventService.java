package com.microservice.manage_event.service.interfaces;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.UpdateEventDTO;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event getEvent(String idEvent);
    List<Event> getEvents();
    State createEvent(CreateEventDTO createEventDTO);
    State deleteEvent(String idEvent);
    State updateEvent(UpdateEventDTO updateEventDTO, String id);
    List<Event> getFilteredEvents(String name, LocalDateTime startDate, LocalDateTime endDate, String address, int capacity);
}
