package com.microservice.manage_event.service.interfaces;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event getEvent(String idEvent);
    List<Event> getEvents();
    State createEvent(CreateEventDTO createEventDTO, LocationVO location, List<LocalityVO> localities);

    State deleteEvent(String idEvent) throws ResourceNotFoundException;
    State updateEvent(UpdateEventDTO updateEventDTO, String id);
    List<Event> filterEvents(String name, LocalDate startDate, LocalDate endDate, String address, Integer capacity);
    List<GlobalEventStatsDTO> getEventStatistics();
    State createLocality(String idEvent, CreateLocalityDTO newLocality);
    State deleteLocality(String idEvent, String idLocality);
    State updateLocality(String idEvent, String idLocality, UpdateLocalityDTO updateLocalityDTO);

    List<ListEventStatsDTO> getStatisticsByEvent();
}
