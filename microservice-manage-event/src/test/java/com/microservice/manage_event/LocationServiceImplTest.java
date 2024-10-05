package com.microservice.manage_event;

import com.microservice.manage_event.persistence.model.entities.Location;
import com.microservice.manage_event.persistence.repository.LocationRepository;
import com.microservice.manage_event.service.implementation.LocationServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class LocationServiceImplTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLocations_Success() {
        // Arrange
        Location location = new Location();
        location.setId(new ObjectId());
        when(locationRepository.findAll()).thenReturn(Collections.singletonList(location));

        // Act
        List<Location> locations = locationService.getLocations();

        // Assert
        assertNotNull(locations);
        assertEquals(1, locations.size());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void testGetLocationById_Success() {
        // Arrange
        ObjectId locationId = new ObjectId();
        Location location = new Location();
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        // Act
        Location foundLocation = locationService.getLocationById(locationId);

        // Assert
        assertNotNull(foundLocation);
        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    void testGetLocationById_NotFound() {
        // Arrange
        ObjectId locationId = new ObjectId();
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> {
            locationService.getLocationById(locationId);
        });
        assertEquals("Location does not exist", exception.getMessage());
    }

    @Test
    void testGetLocationById_InvalidId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.getLocationById(null); // Test with null
        });
    }
}
