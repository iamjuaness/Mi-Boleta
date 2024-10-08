package com.microservice.cart.utils;

import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.presentation.dto.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AppUtil {

    public static State getState(ResponseEntity<MessageDTO<State>> stateAdd, String infoNull) {
        MessageDTO<State> stateMessageDTO = stateAdd.getBody();

        if (stateMessageDTO == null || stateMessageDTO.getData() == null) throw new NullPointerException(infoNull);

        if (stateMessageDTO.getData() != State.SUCCESS){
            return State.ERROR;
        }
        return State.SUCCESS;
    }

}
