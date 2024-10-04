package com.microservice.manage_event.persistence.repository;

import com.microservice.manage_event.persistence.model.entities.Location;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<Location, ObjectId> {
}
