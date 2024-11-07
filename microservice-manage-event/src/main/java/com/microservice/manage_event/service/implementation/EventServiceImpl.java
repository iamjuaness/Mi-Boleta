package com.microservice.manage_event.service.implementation;

import com.microservice.manage_event.client.ManageUserClient;
import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import com.microservice.manage_event.persistence.repository.EventRepository;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.*;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.exception.ErrorResponseException;
import com.microservice.manage_event.service.interfaces.EventService;
import com.microservice.manage_event.utils.mapper.EventMapper;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.vo.EventVO;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class EventServiceImpl implements EventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final MongoTemplate mongoTemplate;
    final ImagesServiceImpl imagesService;
    final ManageUserClient manageUserClient;

    private static final String NOT_FOUND = "Event not found";
    private static final String ID_NOT_VALID = "Id is not valid";
    private static final String PARAMETER_NOT_VALID = "Parameter are not valid";

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, MongoTemplate mongoTemplate, ImagesServiceImpl imagesService, ManageUserClient manageUserClient) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.mongoTemplate = mongoTemplate;
        this.imagesService = imagesService;
        this.manageUserClient = manageUserClient;
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

            // Upload images to Cloudinary and get URLs
            Map<String, String> imageLinks = new HashMap<>();

            if (createEventDTO.getImages() != null && !createEventDTO.getImages().isEmpty()) {
                for (MultipartFile image : createEventDTO.getImages()) {
                    // Rename the file
                    String originalFilename = image.getOriginalFilename();

                    if (originalFilename == null) throw new NullPointerException();

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
    public State deleteEvent(String idEvent) throws ResourceNotFoundException {
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
    }

    /**
     * This method is used for updateEvent
     * @param updateEventDTO update info event
     * @param id event's id
     * @return state action
     */
    @Override
    public State updateEvent(UpdateEventDTO updateEventDTO, String id) {
        // Validates that updateUserDTO is not null
        if (updateEventDTO == null){
            throw new NullPointerException("updateEventDTO cannot be null.");
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
    public List<Event> filterEvents(String name, LocalDate startDate, LocalDate endDate, String address, Integer capacity) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Filtrado por nombre
        if (name == null) throw new NullPointerException(PARAMETER_NOT_VALID);

        if (name.isEmpty()) throw new IllegalArgumentException(PARAMETER_NOT_VALID);

        String[] nameParts = name.split(" ");
        List<Criteria> nameCriteriaList = new ArrayList<>();

        for (String part : nameParts) {
            nameCriteriaList.add(Criteria.where("name").regex(part, "i"));
        }

        if (!nameCriteriaList.isEmpty()) {
            criteriaList.add(new Criteria().orOperator(nameCriteriaList.toArray(new Criteria[0])));
        }

        // Filtrado por fecha de inicio
        if (startDate != null) {
            criteriaList.add(Criteria.where("startDate").gte(startDate));
        }

        // Filtrado por fecha de finalización
        if (endDate != null) {
            criteriaList.add(Criteria.where("endDate").lte(endDate));
        }

        // Filtrado por dirección
        if (address != null && !address.isEmpty()) {
            String[] addressParts = address.split(" ");
            List<Criteria> addressCriteriaList = new ArrayList<>();
            for (String part : addressParts) {
                addressCriteriaList.add(Criteria.where("address").regex(part, "i"));
            }
            if (!addressCriteriaList.isEmpty()) {
                criteriaList.add(new Criteria().orOperator(addressCriteriaList.toArray(new Criteria[0])));
            }
        }

        // Filtrado por capacidad
        if (capacity != null) {
            criteriaList.add(Criteria.where("capacity").gte(capacity));
        }

        // Combina todas las condiciones en el query
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
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
            int totalCapacity = event.getLocalitiesEvent().stream()
                    .filter(Objects::nonNull)  // Filtra los valores nulos si es necesario
                    .mapToInt(LocalityVO::getCapacityLocality)  // Suponiendo que tienes un método getCapacity
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

    @Override
    public List<EventRecommendationDTO> recommendEvents(String userId) {
        // Obtener usuario actual
        ResponseEntity<MessageDTO<User>> userResponse = manageUserClient.getUser(userId);
        User currentUser = Optional.ofNullable(userResponse.getBody())
                .map(MessageDTO::getData)
                .orElseThrow(() -> new NullPointerException("Usuario no encontrado"));

        // Obtener todos los eventos activos
        List<Event> activeEvents = eventRepository.findByDateAfterAndStatusActive(LocalDateTime.now());

        // Calcular puntuación para cada evento
        List<EventRecommendationDTO> recommendations = new ArrayList<>();

        for (Event event : activeEvents) {
            double score = calculateRecommendationScore(event, currentUser);

            if (score > 0) {
                recommendations.add(new EventRecommendationDTO(
                        event.getIdEvent(),
                        event.getName(),
                        score
                ));
            }
        }

        // Ordenar por puntuación descendente
        recommendations.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return recommendations.subList(0, Math.min(10, recommendations.size()));
    }

    private double calculateRecommendationScore(Event event, User user) {
        double score = 0.0;

        // Factor de ubicación (30% del peso)
        if (user.getAddress().equals(event.getLocations().getCity())) {
            score += 30;
        }

        List<String> eventNames = user.getEventsUser()
                .stream()
                .map(EventVO::getName)
                .toList();

        // Factor de categorías preferidas (40% del peso)
        Set<String> userPreferredCategories = new HashSet<>(eventNames);
        if (userPreferredCategories.contains(event.getName())) {
            score += 40;
        }

        // Factor de historial de eventos (30% del peso)
        List<EventVO> userEventHistory = user.getEventsUser();
        if (userEventHistory != null && !userEventHistory.isEmpty()) {
            // Si el usuario ha asistido a eventos similares
            long similarEventsAttended = userEventHistory.stream()
                    .filter(e -> e.getName().equals(event.getName()))
                    .count();
            score += Math.min(similarEventsAttended * 5, 30); // Máximo 30 puntos
        }

        return score;
    }
}
