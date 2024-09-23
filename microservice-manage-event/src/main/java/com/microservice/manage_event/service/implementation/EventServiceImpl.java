package com.microservice.manage_event.service.implementation;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.repository.EventRepository;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.UpdateEventDTO;
import com.microservice.manage_event.service.exception.ErrorResponseException;
import com.microservice.manage_event.service.interfaces.EventService;
import com.microservice.manage_event.utils.mapper.EventMapper;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventServiceImpl implements EventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final MongoTemplate mongoTemplate;

    private static final String NOT_FOUND = "Event not found";
    private static final String ID_NOT_VALID = "Id is not valid";
    private static final String PARAMETER_NOT_VALID = "Parameter are not valid";

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, MongoTemplate mongoTemplate) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.mongoTemplate = mongoTemplate;
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
     * This method is used for createEvent
     * @param createEventDTO create info event
     * @return state action
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

    /**
     * This method is used for deleteEvent
     * @param idEvent event's id
     * @return state action
     */
    @Override
    public State deleteEvent(String idEvent) {
        try {
            if (!StringUtils.hasText(idEvent)){
                throw new IllegalArgumentException(ID_NOT_VALID);
            }

            Query query = new Query();
            query.addCriteria(Criteria.where("_idEvent").is(idEvent));

            Update update = new Update().set("state", State.INACTIVE);

            UpdateResult result = mongoTemplate.updateFirst(query, update, Event.class);

            if (result.getMatchedCount() == 0){
                throw new ResourceNotFoundException(NOT_FOUND);
            }

            if (result.getModifiedCount() == 0) {
                throw new ErrorResponseException("Failed changing code");
            }

            return State.SUCCESS;
        } catch (IllegalArgumentException | ResourceNotFoundException | ErrorResponseException e){
            return State.ERROR;
        }
    }

    /**
     * This method is used for updateEvent
     * @param updateEventDTO update info event
     * @param id event's id
     * @return state action
     */
    @Override
    public State updateEvent(UpdateEventDTO updateEventDTO, String id) {
        try {
            // Validates that updateUserDTO is not null
            if (updateEventDTO == null){
                throw new IllegalArgumentException("updateEventDTO cannot be null.");
            }

            if (!StringUtils.hasText(id)){
                throw new IllegalArgumentException(ID_NOT_VALID);
            }

            // Gets the event that is in the database
            Event event = getEvent(id);

            // A boolean variable is defined as needsUpdate and is initializing as false
            boolean needsUpdate = false;

            // Validates if name of the event changed
            if (!updateEventDTO.nameEvent().equals(event.getName())) {
                event.setName(updateEventDTO.nameEvent());
                needsUpdate = true;
            }

            // Validates if address of the event changed
            if (!updateEventDTO.address().equals(event.getAddress())) {
                event.setAddress(updateEventDTO.address());
                needsUpdate = true;
            }

            // Validates if phoneNumber of the event changed
            if (!updateEventDTO.startDate().equals(event.getStartDate())) {
                event.setStartDate(updateEventDTO.startDate());
                needsUpdate = true;
            }

            // Validate if emailAddress of the event changed
            if (!updateEventDTO.endDate().equals(event.getEndDate())) {
                event.setEndDate(updateEventDTO.endDate());
                needsUpdate = true;
            }

            // Only saves the information if the parameters changed
            if (needsUpdate) {
                eventRepository.save(event);
            }
            return State.SUCCESS;
        } catch (IllegalArgumentException e) {
            return State.ERROR;
        }
    }
}
