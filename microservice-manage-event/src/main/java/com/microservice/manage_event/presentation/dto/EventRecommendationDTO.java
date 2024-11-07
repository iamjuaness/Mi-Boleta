package com.microservice.manage_event.presentation.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventRecommendationDTO {
    @EqualsAndHashCode.Include
    private String eventId;
    private String name;
    private double score;
}
