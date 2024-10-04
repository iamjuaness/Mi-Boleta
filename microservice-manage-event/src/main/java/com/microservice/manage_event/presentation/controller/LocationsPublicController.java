package com.microservice.manage_event.presentation.controller;

import com.microservice.manage_event.persistence.model.entities.Location;
import com.microservice.manage_event.presentation.advice.CustomClientException;
import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_event.service.implementation.LocationServiceImpl;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationsPublicController {

    final LocationServiceImpl locationService;

    public LocationsPublicController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/get-locations")
    public ResponseEntity<MessageDTO<List<Location>>> getAllLocations(){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, locationService.getLocations()));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, Collections.emptyList(), e.getMessage()));
        }
    }

    @GetMapping("/get-location-by-id")
    public ResponseEntity<MessageDTO<Location>> getLocationById(@RequestParam ObjectId locationId){
        try {
            return ResponseEntity.ok().body(new MessageDTO<>(false, locationService.getLocationById(locationId)));
        } catch (CustomClientException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageDTO<>(true, new Location(), e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageDTO<>(true, new Location(), e.getMessage()));
        }
    }
}
