package com.microservice.manage_event.persistence.repository;

import com.microservice.manage_event.persistence.model.entities.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    @Query("{ $and: [ "
            + "{ 'name': { $regex: ?0, $options: 'i' } }, "
            + "{ 'startDate': { $gte: ?1 } }, "
            + "{ 'endDate': { $lte: ?2 } }, "
            + "{ 'address': { $regex: ?3, $options: 'i' } }, "
            + "{ 'capacity': { $gte: ?4 } }, "
            + "{ $expr: { $lt: ['$ticketsSold', '$capacity'] } } "
            + "] }")
    List<Event> findEventsByFilters(String name, LocalDateTime startDate, LocalDateTime endDate, String address, int capacity);
}
