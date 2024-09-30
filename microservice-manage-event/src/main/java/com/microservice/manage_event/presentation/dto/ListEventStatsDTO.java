package com.microservice.manage_event.presentation.dto;

import org.bson.types.ObjectId;

public record ListEventStatsDTO(
        ObjectId idEvent,
        String name,
        int totalEvents,
        int totalTicketsSold,
        int totalCapacity,
        int totalAvailableTickets
) {
}
