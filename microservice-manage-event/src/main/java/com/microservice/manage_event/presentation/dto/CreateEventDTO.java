package com.microservice.manage_event.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDTO(
        @NotBlank(message = "nameEvent is required") String nameEvent,
        @NotBlank(message = "startDate is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @NotBlank(message = "endDate is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,
        @NotEmpty(message = "images are required") List<MultipartFile> images,
        @NotEmpty(message = "locations are required") List<LocationVO> locations,
        @NotBlank(message = "address is required") String address,
        @NotEmpty(message = "localitiesEvent are required") List<LocalityVO> localitiesEvent
) {
}
