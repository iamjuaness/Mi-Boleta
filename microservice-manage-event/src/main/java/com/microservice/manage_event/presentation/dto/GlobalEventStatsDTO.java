package com.microservice.manage_event.presentation.dto;

public record GlobalEventStatsDTO(
        int totalEvents,
        int totalTicketsSold,
        int totalCapacity,
        int totalAvailableTickets
) {
}
