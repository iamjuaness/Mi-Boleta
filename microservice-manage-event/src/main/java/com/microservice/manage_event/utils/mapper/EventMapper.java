package com.microservice.manage_event.utils.mapper;

import com.microservice.manage_event.persistence.model.entities.Event;
import com.microservice.manage_event.persistence.model.enums.State;
import com.microservice.manage_event.persistence.model.vo.LocalityVO;
import com.microservice.manage_event.presentation.dto.CreateEventDTO;
import com.microservice.manage_event.presentation.dto.CreateLocalityDTO;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {


    public Event createEventDTOToEventEntity(CreateEventDTO createEventDTO){
        Event event = new Event();

        event.setName(createEventDTO.nameEvent());
        event.setStartDate(createEventDTO.startDate());
        event.setEndDate(createEventDTO.endDate());
        event.setLocations(createEventDTO.locations());
        event.setAddress(createEventDTO.address());
        event.setState(State.ACTIVE);
        event.setLocalitiesEvent(createEventDTO.localitiesEvent());

        return event;
    }

    public LocalityVO createLocalityDTOToLocalityVO(CreateLocalityDTO createLocalityDTO){
        LocalityVO localityVO = new LocalityVO();

        localityVO.setIdLocality(createLocalityDTO.idLocality());
        localityVO.setNameLocality(createLocalityDTO.nameLocality());
        localityVO.setCapacityLocality(createLocalityDTO.capacityLocality());
        localityVO.setPriceLocality(createLocalityDTO.priceLocality());

        return localityVO;
    }
}
