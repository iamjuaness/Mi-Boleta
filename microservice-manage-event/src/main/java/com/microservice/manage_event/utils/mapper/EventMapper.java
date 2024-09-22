package com.microservice.manage_event.utils.mapper;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;

public class EventMapper {


    public Event createEventDTOToEventEntity(CreateEventDTO createEventDTO){
        Event event = new Event();

        event.setName(createEventDTO.nameEvent());
        event.setStartDate(createEventDTO.startDate());
        event.setEndDate(createEventDTO.endDate());
        event.setImages(createEventDTO.images());
        event.setLocations(createEventDTO.locations());
        event.setAddress(createEventDTO.address());
        event.setState(State.ACTIVE);
        event.setLocalitiesEvent(createEventDTO.localitiesEvent());

        return event;
    }
}
