package com.microservice.manage_event.service.implementation;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import com.microservice.manage_event.persistence.repository.EventRepository;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.*;
import com.microservice.manage_event.service.exception.ErrorResponseException;
import com.microservice.manage_event.service.interfaces.EventService;
import com.microservice.manage_event.utils.mapper.EventMapper;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class EventServiceImpl implements EventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final MongoTemplate mongoTemplate;
    final ImagesServiceImpl imagesService;

    private static final String NOT_FOUND = "Event not found";
    private static final String ID_NOT_VALID = "Id is not valid";
    private static final String PARAMETER_NOT_VALID = "Parameter are not valid";

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, MongoTemplate mongoTemplate, ImagesServiceImpl imagesService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.mongoTemplate = mongoTemplate;
        this.imagesService = imagesService;
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
    public State createEvent(CreateEventDTO createEventDTO, LocationVO location, List<LocalityVO> localities) {
        try {

            // Validate createEventDto is not null
            if (createEventDTO == null){
                throw new IllegalArgumentException(PARAMETER_NOT_VALID);
            }

            // Mapper dto to event
            Event event = eventMapper.createEventDTOToEventEntity(createEventDTO);
            event.setLocations(location);
            event.setLocalitiesEvent(localities);

            // Calculate total capacity by summing up the capacity of all localities
            int totalCapacity = localities
                    .stream()
                    .mapToInt(LocalityVO::getCapacityLocality)
                    .sum();

            // Set the calculated total capacity and initialize ticketsSold to 0
            event.setCapacity(totalCapacity);

            // Save in the repository
            eventRepository.save(event);

            // Upload images to Cloudinary and get URLs
            Map<String, String> imageLinks = new HashMap<>();

            if (createEventDTO.getImages() != null && !createEventDTO.getImages().isEmpty()) {
                for (MultipartFile image : createEventDTO.getImages()) {
                    // Rename the file
                    String originalFilename = image.getOriginalFilename();
                    String newFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')).replace(".", "_").replace(" ", "_");

                    // Upload image
                    Map<?, ?> uploadResult = imagesService.uploadImage(image);
                    String imageUrl = (String) uploadResult.get("secure_url");

                    // Add link to the event
                    imageLinks.put(newFilename, imageUrl);
                }
            }

            event.setImages(imageLinks);

            // Update in the repository
            eventRepository.save(event);

            // return success state
            return State.SUCCESS;
        } catch (IllegalArgumentException | IOException e){
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

    /**
     * This method is used for to filter events
     * @param name event's name
     * @param startDate event's startDate
     * @param endDate event's endDate
     * @param address event's address
     * @param capacity event's capacity
     * @return event's list filtered
     */
    @Override
    public List<Event> filterEvents(String name, LocalDateTime startDate, LocalDateTime endDate, String address, Integer capacity) {
        Query query = new Query();

        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ");
            Criteria nameCriteria = new Criteria();
            for (String part : nameParts){
                nameCriteria.orOperator(Criteria.where("name").regex(part, "i"));
            }
            query.addCriteria(nameCriteria);
        }
        if (startDate != null) {
            query.addCriteria(Criteria.where("startDate").gte(startDate));
        }
        if (endDate != null) {
            query.addCriteria(Criteria.where("endDate").lte(endDate));
        }
        if (address != null && !address.isEmpty()) {
            String[] addressParts = address.split(" ");
            Criteria addressCriteria = new Criteria();
            for (String part : addressParts) {
                addressCriteria.orOperator(Criteria.where("address").regex(part, "i")); // Agregar cada parte como condición "OR"
            }
            query.addCriteria(addressCriteria);
        }
        if (capacity != null) {
            query.addCriteria(Criteria.where("capacity").gte(capacity));
        }

        return mongoTemplate.find(query, Event.class);
    }

    /**
     * This method is used for get global Event Stats
     * @return Event's global stats
     */
    @Override
    public List<GlobalEventStatsDTO> getEventStatistics() {
        return eventRepository.getGlobalEventStats();
    }

    /**
     * This method is used for create locality in an event
     * @param idEvent event's id
     * @param newLocality new locality
     * @return state action
     */
    @Override
    public State createLocality(String idEvent, CreateLocalityDTO newLocality) {

        Optional<Event> optionalEvent = eventRepository.findById(idEvent);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();

            LocalityVO localityVO = eventMapper.createLocalityDTOToLocalityVO(newLocality);

            // Add new locality to the event
            event.getLocalitiesEvent().add(localityVO);

            // Update the total capacity of event
            int totalCapacity = event.getLocalitiesEvent()
                    .stream()
                    .mapToInt(LocalityVO::getCapacityLocality)
                    .sum();
            event.setCapacity(totalCapacity);

            // Save event with new locality
            eventRepository.save(event);
            return State.SUCCESS;
        } else {
            return State.ERROR;
        }
    }

    /**
     * This method is used for delete an event's locality
     * @param idEvent event's id
     * @param idLocality locality's id
     * @return state action
     */
    @Override
    public State deleteLocality(String idEvent, String idLocality) {
        Optional<Event> optionalEvent = eventRepository.findById(idEvent);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();

            // Delete locality by id
            boolean removed = event.getLocalitiesEvent().removeIf(loc -> loc.getIdLocality().equals(idLocality));

            if (removed) {
                // Recalculate the total capacity
                int totalCapacity = event.getLocalitiesEvent()
                        .stream()
                        .mapToInt(LocalityVO::getCapacityLocality)
                        .sum();
                event.setCapacity(totalCapacity);

                eventRepository.save(event);
                return State.SUCCESS;
            }
        }
        return State.ERROR;
    }

    /**
     * This method is used for updateLocality in an event
     * @param idEvent event's id
     * @param idLocality locality's id
     * @param updatedLocalityDTO updated localityDTO
     * @return state action
     */
    @Override
    public State updateLocality(String idEvent, String idLocality, UpdateLocalityDTO updatedLocalityDTO) {
        if (!StringUtils.hasText(idEvent) || !StringUtils.hasText(idLocality)){
            throw new IllegalArgumentException(PARAMETER_NOT_VALID);
        }

        Optional<Event> optionalEvent = eventRepository.findById(idEvent);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();

            // Search locality to modify
            LocalityVO locality = event.getLocalitiesEvent().stream()
                    .filter(loc -> loc.getIdLocality().equals(idLocality))
                    .findFirst()
                    .orElse(null);

            if (locality != null) {
                // Update values of the locality
                locality.setNameLocality(updatedLocalityDTO.nameLocality());
                locality.setCapacityLocality(updatedLocalityDTO.capacityLocality());
                locality.setPriceLocality(updatedLocalityDTO.priceLocality());

                // Recalculate the total capacity of the event
                int totalCapacity = event.getLocalitiesEvent()
                        .stream()
                        .mapToInt(LocalityVO::getCapacityLocality)
                        .sum();
                event.setCapacity(totalCapacity);

                eventRepository.save(event);
                return State.SUCCESS;
            }
        }
        return State.ERROR;
    }

    /**
     * This method is used for get Stats By Event
     * @return Event's stats
     */
    @Override
    public List<ListEventStatsDTO> getStatisticsByEvent() {
        return eventRepository.getEventStatsByEvent();
    }
}
