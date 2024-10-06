package com.microservice.cart.service.implementation;

import com.microservice.cart.client.ManageUserClient;
import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.persistence.model.vo.EventVO;
import com.microservice.cart.presentation.dto.AddToCartDTO;
import com.microservice.cart.presentation.dto.MessageDTO;
import com.microservice.cart.presentation.dto.PurchaseOrderDTO;
import com.microservice.cart.service.interfaces.CartService;
import com.microservice.cart.utils.mapper.VOToDTOMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final String PARAMETER_NOT_VALID = "PARAMETER_NOT_VALID";
    private static final String ID_NOT_VALID = "ID_NOT_VALID";
    private static final String INFO_NULL = "INFO_NULL";

    final ManageUserClient manageUserClient;
    final VOToDTOMapper mapper;

    public CartServiceImpl(ManageUserClient manageUserClient, VOToDTOMapper mapper) {
        this.manageUserClient = manageUserClient;
        this.mapper = mapper;
    }

    /**
     * This method is used for add to cart of the user
     * @param idUser user's id
     * @param eventVO item for add to cart
     * @return state action
     */
    @Override
    public State addToCart(String idUser, EventVO eventVO) {

        if (!StringUtils.hasText(idUser)) throw new IllegalArgumentException(ID_NOT_VALID);

        if (eventVO == null) throw new IllegalArgumentException(PARAMETER_NOT_VALID);

        AddToCartDTO addToCartDTO = mapper.eventVOToAddToCartDTO(eventVO);

        ResponseEntity<MessageDTO<State>> stateAdd = manageUserClient.addToCart(addToCartDTO, idUser);
        MessageDTO<State> stateMessageDTO = stateAdd.getBody();

        if (stateMessageDTO == null || stateMessageDTO.getData() == null) throw new NullPointerException(INFO_NULL);

        if (stateMessageDTO.getData() != State.SUCCESS){
            return State.ERROR;
        }
        return State.SUCCESS;
    }

    @Override
    public State deleteToCart(String idUser, String idEventVO) {
        return null;
    }

    @Override
    public State upgradeQuantity(String idUser, String idEventVO, int quantity) {
        return null;
    }

    @Override
    public List<EventVO> getCart(String idUser) {
        return Collections.emptyList();
    }

    @Override
    public State clearCart(String idUser) {
        return null;
    }

    @Override
    public State applyDiscount(String idUser, String discountCode) {
        return null;
    }

    @Override
    public BigDecimal calculateTotal(String idUser) {
        return null;
    }

    @Override
    public PurchaseOrderDTO checkout(String idUser) {
        return null;
    }
}
