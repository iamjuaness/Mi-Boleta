package com.microservice.manage_event.service.interfaces;

import com.microservice.manage_event.persistence.model.entities.Location;
import org.bson.types.ObjectId;

import java.util.List;

public interface LocationService {

    List<Location> getLocations();

    Location getLocationById(ObjectId locationId);
}
