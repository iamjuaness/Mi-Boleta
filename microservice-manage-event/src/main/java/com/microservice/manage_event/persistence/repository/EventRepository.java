package com.microservice.manage_event.persistence.repository;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.presentation.dto.GlobalEventStatsDTO;
import com.microservice.manage_event.presentation.dto.ListEventStatsDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {


    @Aggregation(pipeline = {
            "{ $match: { state: 'ACTIVE' } }",
            "{ $group: { " +
                    "    _id: null, " +
                    "    totalEvents: { $sum: 1 }, " +
                    "    totalTicketsSold: { $sum: '$ticketsSold' }, " +
                    "    totalCapacity: { $sum: '$capacity' }, " +
                    "    totalAvailableTickets: { $sum: { $subtract: ['$capacity', '$ticketsSold'] } } " +
                    "} }"
    })
    List<GlobalEventStatsDTO> getGlobalEventStats();

    @Aggregation(pipeline = {
            "{ $match: { state: 'ACTIVE' } }",
            "{ $project: { " +
                    "    idEvent: '$_id', " +
                    "    name: 1, " +
                    "    totalTicketsSold: '$ticketsSold', " +
                    "    totalCapacity: '$capacity', " +
                    "    totalAvailableTickets: { $subtract: ['$capacity', '$ticketsSold'] }, " +
                    "    totalEvents: { $literal: 1 } " +
                    "} }"
    })
    List<ListEventStatsDTO> getEventStatsByEvent();

    @Query("{'startDate': {$gt: ?0}, 'statusActive': 'ACTIVE'}")
    List<Event> findActiveEventsAfterDate(LocalDateTime startDate);
}
