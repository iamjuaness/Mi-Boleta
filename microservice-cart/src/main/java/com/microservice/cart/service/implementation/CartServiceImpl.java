package com.microservice.cart.service.implementation;

import com.microservice.cart.client.ManageUserClient;
import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.persistence.model.vo.EventVO;
import com.microservice.cart.presentation.dto.AddToCartDTO;
import com.microservice.cart.presentation.dto.MessageDTO;
import com.microservice.cart.presentation.dto.PurchaseOrderDTO;
import com.microservice.cart.service.interfaces.CartService;
import com.microservice.cart.utils.AppUtil;
import com.microservice.cart.utils.mapper.MapperUtil;
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
    private static final String RESPONSE_NOT_VALID = "RESPONSE_NOT_VALID";

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
        return AppUtil.getState(stateAdd, INFO_NULL);
    }

    /**
     * This method is used for delete an item of the cart
     * @param idUser user's id
     * @param idEventVO item's id
     * @return state action
     */
    @Override
    public State deleteToCart(String idUser, String idEventVO) {
        if (!StringUtils.hasText(idUser) || !StringUtils.hasText(idEventVO)) throw new IllegalArgumentException(PARAMETER_NOT_VALID);

        ResponseEntity<MessageDTO<State>> response = manageUserClient.deleteItemCart(idUser, idEventVO);
        return AppUtil.getState(response, RESPONSE_NOT_VALID);
    }

    /**
     * This method is used for upgrade quantity of an item
     * @param idUser user's id
     * @param idEventVO item's id
     * @param quantity new quantity
     * @return state action
     */
    @Override
    public State upgradeQuantity(String idUser, String idEventVO, int quantity) {
        if (!StringUtils.hasText(idUser) || !StringUtils.hasText(idEventVO) || quantity == 0) throw new IllegalArgumentException(PARAMETER_NOT_VALID);

        ResponseEntity<MessageDTO<State>> response = manageUserClient.upgradeQuantity(idUser, idEventVO, quantity);
        return AppUtil.getState(response, RESPONSE_NOT_VALID);
    }

    /**
     * This method is used for get cart of a user
     * @param idUser user's id
     * @return user's cart
     */
    @Override
    public List<EventVO> getCart(String idUser) {
        if (!StringUtils.hasText(idUser)) throw new IllegalArgumentException(ID_NOT_VALID);

        ResponseEntity<MessageDTO<List<AddToCartDTO>>> response = manageUserClient.getCart(idUser);
        MessageDTO<List<AddToCartDTO>> messageDTO = response.getBody();

        if (messageDTO == null || messageDTO.getData() == null) throw new NullPointerException(RESPONSE_NOT_VALID);

        if (messageDTO.getData().isEmpty()) return Collections.emptyList();

        return MapperUtil.mapList(messageDTO.getData(), list -> new EventVO(list.idEventVO(),
                list.idEvent(), list.idLocality(), list.unitValue(), list.quantity()));
    }

    /**
     * This method is used for clear cart of a user
     * @param idUser user's id
     * @return state action
     */
    @Override
    public State clearCart(String idUser) {
        if (!StringUtils.hasText(idUser)) throw new IllegalArgumentException(ID_NOT_VALID);

        ResponseEntity<MessageDTO<State>> response = manageUserClient.clearCart(idUser);
        return AppUtil.getState(response, RESPONSE_NOT_VALID);
    }

    /**
     * This method is used for apply discount to the cart
     * @param idUser user's id
     * @param discountCode code discount
     * @return state action
     */
    @Override
    public State applyDiscount(String idUser, String discountCode) {
        return null;
    }

    /**
     * This method is used for calculate the total of the cart
     * @param idUser user's id
     * @return total of the cart
     */
    @Override
    public BigDecimal calculateTotal(String idUser) {
        if (!StringUtils.hasText(idUser)) throw new IllegalArgumentException(ID_NOT_VALID);

        List<EventVO> list = getCart(idUser);

        return list.stream().map(EventVO::calculateTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * This method is used for create a purchase order and send to the microservice shopping
     * @param idUser user's id
     * @return purchase order
     */
    @Override
    public PurchaseOrderDTO checkout(String idUser) {
        return null;
    }
}
