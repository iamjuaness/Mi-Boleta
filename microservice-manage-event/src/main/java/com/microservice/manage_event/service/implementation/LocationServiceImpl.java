package com.microservice.manage_event.service.implementation;

import com.microservice.manage_event.persistence.model.entities.Location;
import com.microservice.manage_event.persistence.repository.LocationRepository;
import com.microservice.manage_event.service.interfaces.LocationService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(ObjectId locationId) {
        if (!StringUtils.hasText(String.valueOf(locationId))){
            throw new IllegalArgumentException("Id is not valid");
        }
        try {
            Location location = locationRepository.findById(locationId).get();

            if (location == null){
                throw new NullPointerException("Location is not exists");
            }
            return location;
        } catch (IllegalArgumentException | NullPointerException e){
            return new Location();
        }
    }
}
