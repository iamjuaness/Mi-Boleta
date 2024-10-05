package com.microservice.manage_event;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import com.microservice.manage_event.persistence.repository.EventRepository;
import com.microservice.manage_event.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_event.presentation.dto.*;
import com.microservice.manage_event.service.exception.ErrorResponseException;
import com.microservice.manage_event.service.implementation.EventServiceImpl;
import com.microservice.manage_event.service.implementation.ImagesServiceImpl;
import com.microservice.manage_event.utils.mapper.EventMapper;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ImagesServiceImpl imagesService;

    @Mock
    private UpdateResult updateResult;

    @Mock
    private Event event;

    @InjectMocks
    private EventServiceImpl eventService;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 2);
    }

    // Test getEvent - ID válido
    @Test
    void testGetEventValidId() {
        event = new Event();
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));

        Event result = eventService.getEvent("validId");

        assertNotNull(result);
        verify(eventRepository).findById("validId");
    }

    // Test getEvent - ID inválido
    @Test
    void testGetEventInvalidId() {
        Event result = eventService.getEvent(null);
        assertEquals(new Event(), result);
    }

    // Test getEvent - Evento no encontrado
    @Test
    void testGetEventNotFound() {
        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        Event result = eventService.getEvent("invalidId");

        assertEquals(new Event(), result);
    }

    // Test getEvents - Encontrar eventos
    @Test
    void testGetEventsFound() {
        List<Event> events = Arrays.asList(new Event(), new Event());
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getEvents();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    // Test getEvents - No se encontraron eventos
    @Test
    void testGetEventsNotFound() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getEvents();

        assertTrue(result.isEmpty());
    }

    // Test createEvent - Evento válido
    @Test
    void testCreateEventSuccess() {
        CreateEventDTO createEventDTO = mock(CreateEventDTO.class);
        LocationVO location = mock(LocationVO.class);
        List<LocalityVO> localities = Arrays.asList(mock(LocalityVO.class));

        // Simula los valores de los DTO
        when(createEventDTO.getImages()).thenReturn(Collections.emptyList());
        when(localities.get(0).getCapacityLocality()).thenReturn(100);

        // Simula el mapeo del DTO a la entidad Event
        event = new Event();
        when(eventMapper.createEventDTOToEventEntity(any(CreateEventDTO.class))).thenReturn(event);

        // Llama al método createEvent
        State result = eventService.createEvent(createEventDTO, location, localities);

        // Verifica que el estado sea SUCCESS y que el evento se guarde una vez (no hay imágenes)
        assertEquals(State.SUCCESS, result);
        verify(eventRepository).save(event); // Solo se llama una vez
        assertTrue(event.getImages().isEmpty());  // Verifica que no se haya agregado ninguna imagen
    }


    @Test
    void testCreateEventWithImages() throws IOException {
        CreateEventDTO createEventDTO = mock(CreateEventDTO.class);
        LocationVO location = mock(LocationVO.class);
        List<LocalityVO> localities = Arrays.asList(mock(LocalityVO.class));
        MultipartFile image = mock(MultipartFile.class);

        // Simula los valores de los DTO
        when(createEventDTO.getImages()).thenReturn(Arrays.asList(image));
        when(localities.get(0).getCapacityLocality()).thenReturn(100);
        when(image.getOriginalFilename()).thenReturn("test_image.jpg");

        // Simula la subida de la imagen
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test_image.jpg");
        when(imagesService.uploadImage(any(MultipartFile.class))).thenReturn(uploadResult);

        // Simula el mapeo del DTO a la entidad Event
        event = new Event();
        when(eventMapper.createEventDTOToEventEntity(any(CreateEventDTO.class))).thenReturn(event);

        // Llama al método createEvent
        State result = eventService.createEvent(createEventDTO, location, localities);

        // Verifica que el estado sea SUCCESS y que el evento se guarde con las imágenes
        assertEquals(State.SUCCESS, result);
        verify(eventRepository, times(1)).save(event);  // Se guarda dos veces: una al principio y otra después de subir imágenes
        assertEquals(1, event.getImages().size());  // Verifica que se haya agregado la imagen correctamente
    }

    @Test
    void testCreateEventImageUploadFailure() throws IOException {
        CreateEventDTO createEventDTO = mock(CreateEventDTO.class);
        LocationVO location = mock(LocationVO.class);
        List<LocalityVO> localities = Arrays.asList(mock(LocalityVO.class));
        MultipartFile image = mock(MultipartFile.class);

        // Simula los valores de los DTO
        when(createEventDTO.getImages()).thenReturn(Arrays.asList(image));
        when(localities.get(0).getCapacityLocality()).thenReturn(100);
        when(image.getOriginalFilename()).thenReturn("test_image.jpg");

        // Simula el fallo al subir la imagen
        when(imagesService.uploadImage(any(MultipartFile.class))).thenThrow(IOException.class);

        // Simula el mapeo del DTO a la entidad Event
        event = new Event();
        when(eventMapper.createEventDTOToEventEntity(any(CreateEventDTO.class))).thenReturn(event);

        // Llama al método createEvent
        State result = eventService.createEvent(createEventDTO, location, localities);

        // Verifica que el estado sea ERROR debido al fallo en la subida de imágenes
        assertEquals(State.ERROR, result);
        verify(eventRepository, never()).save(event);  // No debe intentar guardar el evento si hubo un fallo
    }


    @Test
    void testCreateEventNullCreateEventDTO() {
        LocationVO location = mock(LocationVO.class);
        List<LocalityVO> localities = Arrays.asList(mock(LocalityVO.class));

        // Llama al método con createEventDTO nulo
        State result = eventService.createEvent(null, location, localities);

        // Verifica que el estado sea ERROR debido a que el DTO es nulo
        assertEquals(State.ERROR, result);
    }


    // Test createEvent - Excepción IllegalArgumentException
    @Test
    void testCreateEventThrowsIllegalArgumentException() {
        State result = eventService.createEvent(null, null, null);
        assertEquals(State.ERROR, result);
    }

    @Test
    void testDeleteEventWithNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent(null);
        });
        assertEquals("Id is not valid", exception.getMessage());
    }

    @Test
    void testDeleteEventWithEmptyId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent("");
        });
        assertEquals("Id is not valid", exception.getMessage());
    }

    @Test
    void testDeleteEventNotFound() {
        // Configura el comportamiento del mock
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Event.class)))
                .thenReturn(updateResult); // Se retorna el mock de UpdateResult

        when(updateResult.getMatchedCount()).thenReturn(0L); // No se encuentra el evento

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            eventService.deleteEvent("invalidId");
        });
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void testDeleteEventUpdateFailed() {
        // Configura el comportamiento del mock
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Event.class)))
                .thenReturn(updateResult); // Se retorna el mock de UpdateResult

        when(updateResult.getMatchedCount()).thenReturn(1L); // Se encuentra el evento
        when(updateResult.getModifiedCount()).thenReturn(0L); // No se realiza la modificación

        Exception exception = assertThrows(ErrorResponseException.class, () -> {
            eventService.deleteEvent("validId");
        });
        assertEquals("Failed changing code", exception.getMessage());
    }

    @Test
    void testDeleteEventSuccess() throws ResourceNotFoundException {
        // Configura el comportamiento del mock
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Event.class)))
                .thenReturn(updateResult); // Se retorna el mock de UpdateResult

        when(updateResult.getMatchedCount()).thenReturn(1L); // Se encuentra el evento
        when(updateResult.getModifiedCount()).thenReturn(1L); // Se realiza la modificación

        State result = eventService.deleteEvent("validId");
        assertEquals(State.SUCCESS, result);
    }

    @Test
    void testUpdateEvent_NullUpdateEventDTO() {
        assertThrows(NullPointerException.class, () -> eventService.updateEvent(null, "valid-id"));
    }

    @Test
    void testUpdateEvent_InvalidID() {
        // Arrange: Prepare the UpdateEventDTO with invalid parameters
        UpdateEventDTO updateEventDTO = new UpdateEventDTO("", null, null, "");

        // Act & Assert: Check that an IllegalArgumentException is thrown when an invalid ID is provided
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(updateEventDTO, ""));

        // Optional: Assert the exception message for clarity
        assertEquals("Id is not valid", exception.getMessage()); // Modify as needed based on your actual exception handling
    }

    @Test
    void testUpdateEvent_EventDoesNotExist() {
        // Arrange: Simulate that the event with the specified ID does not exist
        when(eventRepository.findById("valid-id")).thenReturn(Optional.empty());

        // Act & Assert: Capture the invocation that may throw a NullPointerException
        UpdateEventDTO updateEventDTO = null;
        NullPointerException exception = assertThrows(NullPointerException.class, () -> eventService.updateEvent(updateEventDTO, "valid-id"));

        // Optionally assert the exception message if needed
        assertEquals("updateEventDTO cannot be null.", exception.getMessage()); // Modify as needed based on your exception handling
    }

    @Test
    void testUpdateEvent_NoChanges() {
        UpdateEventDTO dto = new UpdateEventDTO("Event Name", startDate, endDate, "Address");
        when(eventRepository.findById("valid-id")).thenReturn(Optional.of(event));
        when(event.getName()).thenReturn("Event Name");
        when(event.getAddress()).thenReturn("Address");
        when(event.getStartDate()).thenReturn(LocalDate.parse("2023-01-01"));
        when(event.getEndDate()).thenReturn(LocalDate.parse("2023-01-02"));

        assertEquals(State.SUCCESS, eventService.updateEvent(dto, "valid-id"));
        verify(eventRepository, never()).save(any(Event.class)); // No debe guardarse
    }

    @Test
    void testUpdateEvent_ChangeName() {
        UpdateEventDTO dto = new UpdateEventDTO("New Event Name", startDate, endDate, "Address");
        when(eventRepository.findById("valid-id")).thenReturn(Optional.of(event));
        when(event.getName()).thenReturn("Event Name");
        when(event.getAddress()).thenReturn("Address");
        when(event.getStartDate()).thenReturn(LocalDate.parse("2023-01-01"));
        when(event.getEndDate()).thenReturn(LocalDate.parse("2023-01-02"));

        assertEquals(State.SUCCESS, eventService.updateEvent(dto, "valid-id"));
        verify(event).setName("New Event Name");
        verify(eventRepository).save(event); // Debe guardarse
    }

    @Test
    void testUpdateEvent_ChangeAddress() {
        UpdateEventDTO dto = new UpdateEventDTO("Event Name", startDate, endDate, "New Address");
        when(eventRepository.findById("valid-id")).thenReturn(Optional.of(event));
        when(event.getName()).thenReturn("Event Name");
        when(event.getAddress()).thenReturn("Address");
        when(event.getStartDate()).thenReturn(LocalDate.parse("2023-01-01"));
        when(event.getEndDate()).thenReturn(LocalDate.parse("2023-01-02"));

        assertEquals(State.SUCCESS, eventService.updateEvent(dto, "valid-id"));
        verify(event).setAddress("New Address");
        verify(eventRepository).save(event); // Debe guardarse
    }

    @Test
    void testUpdateEvent_ChangeStartDate() {
        UpdateEventDTO dto = new UpdateEventDTO("New Event Name", LocalDate.of(2023, 01, 02), endDate, "Address");
        when(eventRepository.findById("valid-id")).thenReturn(Optional.of(event));
        when(event.getName()).thenReturn("Event Name");
        when(event.getAddress()).thenReturn("Address");
        when(event.getStartDate()).thenReturn(LocalDate.parse("2023-01-01"));
        when(event.getEndDate()).thenReturn(LocalDate.parse("2023-01-02"));

        assertEquals(State.SUCCESS, eventService.updateEvent(dto, "valid-id"));
        verify(event).setStartDate(LocalDate.parse("2023-01-02"));
        verify(eventRepository).save(event); // Debe guardarse
    }

    @Test
    void testUpdateEvent_ChangeEndDate() {
        UpdateEventDTO dto = new UpdateEventDTO("Event Name", startDate, LocalDate.of(2023, 01, 03), "Address");
        when(eventRepository.findById("valid-id")).thenReturn(Optional.of(event));
        when(event.getName()).thenReturn("Event Name");
        when(event.getAddress()).thenReturn("Address");
        when(event.getStartDate()).thenReturn(LocalDate.parse("2023-01-01"));
        when(event.getEndDate()).thenReturn(LocalDate.parse("2023-01-02"));

        assertEquals(State.SUCCESS, eventService.updateEvent(dto, "valid-id"));
        verify(event).setEndDate(LocalDate.parse("2023-01-03"));
        verify(eventRepository).save(event); // Debe guardarse
    }

    // Test createLocality - Localidad creada exitosamente
    @Test
    void testCreateLocalitySuccess() {
        // Inicializa el evento con una lista vacía de localidades
        event = new Event();
        event.setLocalitiesEvent(new ArrayList<>()); // Aseguramos que la lista no sea null

        // Simula el valor encontrado en la base de datos
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));

        // Crea un mock de CreateLocalityDTO
        CreateLocalityDTO newLocality = mock(CreateLocalityDTO.class);

        // Simula los valores necesarios del DTO si es requerido
        when(newLocality.idLocality()).thenReturn("idLocality");

        // Llama al método createLocality
        State result = eventService.createLocality("validId", newLocality);

        // Verifica que el estado sea SUCCESS y que el evento se haya guardado
        assertEquals(State.SUCCESS, result);
        verify(eventRepository).save(any(Event.class));
    }


    // Test createLocality - Evento no encontrado
    @Test
    void testCreateLocalityEventNotFound() {
        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        CreateLocalityDTO newLocality = mock(CreateLocalityDTO.class);
        State result = eventService.createLocality("invalidId", newLocality);

        assertEquals(State.ERROR, result);
    }

    // Test deleteLocality - Localidad eliminada
    @Test
    void testDeleteLocalitySuccess() {
        event = new Event();
        LocalityVO localityVO = new LocalityVO();
        localityVO.setIdLocality("localityId");
        event.setLocalitiesEvent(new ArrayList<>(Collections.singletonList(localityVO)));
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));

        State result = eventService.deleteLocality("eventId", "localityId");

        assertEquals(State.SUCCESS, result);
        verify(eventRepository).save(any(Event.class));
    }

    // Test deleteLocality - Localidad no encontrada
    @Test
    void testDeleteLocalityNotFound() {
        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        State result = eventService.deleteLocality("eventId", "localityId");

        assertEquals(State.ERROR, result);
    }

    @Test
    void testFilterEvents_WithName() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents("Sample Event", startDate, endDate, null, null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithStartDateAndEndDate() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, startDate, endDate, null, null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithAddress() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, null, null, "Sample Address", null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithCapacity() {
        Integer capacity = 100;
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, null, null, null, capacity);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithAllParameters() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents("Sample Event", startDate, endDate, "Sample Address", 100);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithNullOrEmptyParameters() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, null, null, null, null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithOnlyAddressAndStartDate() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, startDate, null, "Sample Address", null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithOnlyNameAndEndDate() {
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents("Sample Event", null, endDate, null, null);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testFilterEvents_WithOnlyCapacityAndAddress() {
        Integer capacity = 100;
        List<Event> expectedEvents = List.of(event);

        when(mongoTemplate.find(any(Query.class), eq(Event.class))).thenReturn(expectedEvents);

        List<Event> result = eventService.filterEvents(null, null, null, "Sample Address", capacity);

        assertEquals(expectedEvents, result);
        verify(mongoTemplate).find(any(Query.class), eq(Event.class));
    }

    @Test
    void testGetEventStatistics() {
        // Arrange: crear datos de prueba
        GlobalEventStatsDTO stat1 = new GlobalEventStatsDTO(10, 200, 1500, 1300);
        GlobalEventStatsDTO stat2 = new GlobalEventStatsDTO(15, 150, 700, 550);
        List<GlobalEventStatsDTO> expectedStats = List.of(stat1, stat2);

        // Configurar el comportamiento simulado del repositorio
        when(eventRepository.getGlobalEventStats()).thenReturn(expectedStats);

        // Act: llamar al método que estamos probando
        List<GlobalEventStatsDTO> result = eventService.getEventStatistics();

        // Assert: verificar que el resultado sea el esperado
        assertEquals(expectedStats, result);
        // Verificar que el método del repositorio fue llamado una vez
        verify(eventRepository, times(1)).getGlobalEventStats();
    }

    @Test
    void testUpdateLocality_Success() {
        // Arrange
        String idEvent = "event123";
        String idLocality = "locality123";
        UpdateLocalityDTO updatedLocalityDTO = new UpdateLocalityDTO("New Locality Name", 100, 50.0);

        event = new Event();
        event.setLocalitiesEvent(List.of(new LocalityVO(idLocality, "Old Locality Name", 50, 20.0)));
        event.setCapacity(50);

        when(eventRepository.findById(idEvent)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        // Act
        State result = eventService.updateLocality(idEvent, idLocality, updatedLocalityDTO);

        // Assert
        assertEquals(State.SUCCESS, result);
        assertEquals("New Locality Name", event.getLocalitiesEvent().get(0).getNameLocality());
        assertEquals(100, event.getLocalitiesEvent().get(0).getCapacityLocality());
        assertEquals(50.0, event.getLocalitiesEvent().get(0).getPriceLocality());
        assertEquals(100, event.getCapacity()); // Total capacity should be updated
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testUpdateLocality_EventNotFound() {
        // Arrange
        String idEvent = "nonExistentEvent";
        String idLocality = "locality123";
        UpdateLocalityDTO updatedLocalityDTO = new UpdateLocalityDTO("New Locality Name", 100, 50.0);

        when(eventRepository.findById(idEvent)).thenReturn(Optional.empty());

        // Act
        State result = eventService.updateLocality(idEvent, idLocality, updatedLocalityDTO);

        // Assert
        assertEquals(State.ERROR, result);
        verify(eventRepository, never()).save(any(Event.class)); // No save should be called
    }

    @Test
    void testUpdateLocality_LocalityNotFound() {
        // Arrange
        String idEvent = "event123";
        String idLocality = "nonExistentLocality";
        UpdateLocalityDTO updatedLocalityDTO = new UpdateLocalityDTO("New Locality Name", 100, 50.0);

        event = new Event();
        event.setLocalitiesEvent(List.of(new LocalityVO("locality123", "Old Locality Name", 50, 20.0)));
        event.setCapacity(50);

        when(eventRepository.findById(idEvent)).thenReturn(Optional.of(event));

        // Act
        State result = eventService.updateLocality(idEvent, idLocality, updatedLocalityDTO);

        // Assert
        assertEquals(State.ERROR, result);
        verify(eventRepository, never()).save(any(Event.class)); // No save should be called
    }

    @Test
    void testUpdateLocality_InvalidParameters() {
        // Arrange
        String idEvent = "";
        String idLocality = "";
        UpdateLocalityDTO updatedLocalityDTO = new UpdateLocalityDTO("New Locality Name", 100, 50.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateLocality(idEvent, idLocality, updatedLocalityDTO);
        });
    }

    @Test
    void testGetStatisticsByEvent() {
        // Arrange
        List<ListEventStatsDTO> expectedStats = Arrays.asList(
                new ListEventStatsDTO(null,"Event 1", 10, 100, 1500, 1400),
                new ListEventStatsDTO(null,"Event 2", 5, 50, 300, 250)
        );

        when(eventRepository.getEventStatsByEvent()).thenReturn(expectedStats);

        // Act
        List<ListEventStatsDTO> actualStats = eventService.getStatisticsByEvent();

        // Assert
        assertEquals(expectedStats, actualStats);
        verify(eventRepository, times(1)).getEventStatsByEvent(); // Verify method is called once
    }

    @Test
    void testGetStatisticsByEvent_EmptyList() {
        // Arrange
        List<ListEventStatsDTO> expectedStats = Arrays.asList();

        when(eventRepository.getEventStatsByEvent()).thenReturn(expectedStats);

        // Act
        List<ListEventStatsDTO> actualStats = eventService.getStatisticsByEvent();

        // Assert
        assertEquals(expectedStats, actualStats);
        verify(eventRepository, times(1)).getEventStatsByEvent(); // Verify method is called once
    }
}

