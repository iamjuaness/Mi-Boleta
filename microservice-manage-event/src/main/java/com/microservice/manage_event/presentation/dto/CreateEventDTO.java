package com.microservice.manage_event.presentation.dto;

import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDTO(
        @NotBlank(message = "nameEvent is required") String nameEvent,
        @NotBlank(message = "startDate is required") LocalDateTime startDate,
        @NotBlank(message = "endDate is required") LocalDateTime endDate,
        @NotEmpty(message = "images are required") List<String> images,
        @NotEmpty(message = "locations are required") List<LocationVO> locations,
        @NotBlank(message = "address is required") String address,
        @NotEmpty(message = "localitiesEvent are required") List<LocalityVO> localitiesEvent
) {
}
