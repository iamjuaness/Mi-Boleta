package com.microservice.cart.utils.mapper;

import com.microservice.cart.persistence.model.vo.EventVO;
import com.microservice.cart.presentation.dto.AddToCartDTO;
import org.springframework.stereotype.Service;

@Service
public class VOToDTOMapper {

    public AddToCartDTO eventVOToAddToCartDTO(EventVO eventVO){
        return new AddToCartDTO(eventVO.getIdEventVO(), eventVO.getIdEvent(),
                eventVO.getIdLocality(), eventVO.getUnitValue(), eventVO.getQuantity());
    }
}
