package com.microservice.manage_event.persistence.model.entities;

import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.persistence.model.vo.LocationVO;
import com.microservice.manage_event.persistence.model.vo.UserVO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("events")
public class Event {

    @Id
    @EqualsAndHashCode.Include
    private String idEvent;
    private String name;
    private State state;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, String> images;
    private List<LocationVO> locations;
    private String address;
    private int capacity;
    private int ticketsSold;
    private List<LocalityVO> localitiesEvent;
    private List<UserVO> registeredUsers;
}
