package com.microservice.manage_event.persistence.repository;

import com.microservice.manage_event.persistence.model.entities.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
