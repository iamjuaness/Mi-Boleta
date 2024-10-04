package com.microservice.manage_event.presentation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateEventDTO {
        @NotBlank(message = "nameEvent is required")
        private String nameEvent;

        @NotBlank(message = "startDate is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private String startDate;

        @NotBlank(message = "endDate is required")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private String endDate;

        @NotEmpty(message = "images are required")
        private List<MultipartFile> images;

        @NotEmpty(message = "location is required")
        private String location;  // This will be a JSON string

        @NotBlank(message = "address is required")
        private String address;

        @NotEmpty(message = "localitiesEvent are required")
        private String localitiesEvent;  // This will be a JSON string


        private static final ObjectMapper objectMapper = new ObjectMapper();

        public LocalDate getStartDateAsLocalDate() {
                return LocalDate.parse(this.startDate);
        }

        public LocalDate getEndDateAsLocalDate() {
                return LocalDate.parse(this.endDate);
        }

        public LocationVO getLocationObject() {
                try {
                        return objectMapper.readValue(this.location, LocationVO.class);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing location JSON", e);
                }
        }

        public List<LocalityVO> getLocalitiesEventObjects() {
                try {
                        return objectMapper.readValue(this.localitiesEvent, new TypeReference<List<LocalityVO>>() {});
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing localitiesEvent JSON", e);
                }
        }
}