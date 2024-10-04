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

        event.setName(createEventDTO.getNameEvent());
        event.setStartDate(createEventDTO.getStartDateAsLocalDate());
        event.setEndDate(createEventDTO.getEndDateAsLocalDate());
        event.setAddress(createEventDTO.getAddress());
        event.setState(State.ACTIVE);

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
