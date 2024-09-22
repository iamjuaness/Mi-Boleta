package com.microservice.manage_event.service.implementation;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.repository.EventRepository;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.UpdateEventDTO;
import com.microservice.manage_event.service.interfaces.EventService;
import com.microservice.manage_event.utils.mapper.EventMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventServiceImpl implements EventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;

    private static final String NOT_FOUND = "Event not found";
    private static final String ID_NOT_VALID = "Id is not valid";
    private static final String PARAMETER_NOT_VALID = "Parameter are not valid";

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * This method is used for get event by its id
     * @param idEvent event's id
     * @return event
     */
    @Override
    public Event getEvent(String idEvent) {
        try {
            // Validate idEvent
            if (!StringUtils.hasText(idEvent)){
                throw new IllegalArgumentException(ID_NOT_VALID);
            }

            //Get Event by id
            Optional<Event> event = eventRepository.findById(idEvent);

            // Validate if event exists
            if (event.isEmpty()){
                throw new ResourceNotFoundException(NOT_FOUND);
            }

            // Return event
            return event.get();

        } catch (IllegalArgumentException | ResourceNotFoundException e){
            // return event empty
            return new Event();
        }
    }

    /**
     * This method is used for get all events
     * @return event list
     */
    @Override
    public List<Event> getEvents() {
        try {
            // Get events of the database
            List<Event> events = eventRepository.findAll();

            // Validate that event is no empty
            if (events.isEmpty()){
                throw new ResourceNotFoundException("Events not found");
            }

            // return event list
            return events;
        } catch (ResourceNotFoundException e){
            //return list empty
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param createEventDTO
     * @return
     */
    @Override
    public State createEvent(CreateEventDTO createEventDTO) {
        try {

            // Validate createEventDto is not null
            if (createEventDTO == null){
                throw new IllegalArgumentException(PARAMETER_NOT_VALID);
            }

            // Mapper dto to event
            Event event = eventMapper.createEventDTOToEventEntity(createEventDTO);

            // Save in the repository
            eventRepository.save(event);

            // return success state
            return State.SUCCESS;
        } catch (IllegalArgumentException e){

            //return error state
            return State.ERROR;
        }
    }

    @Override
    public State deleteEvent(String idEvent) {
        return null;
    }

    @Override
    public State updateEvent(UpdateEventDTO updateEventDTO, String id) {
        return null;
    }
}
