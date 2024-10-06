package com.microservice_shopping.persistence.model.entities;


import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.persistence.model.vo.EventVO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("purchasesOrders")
public class PurchaseOrder {

    @Id
    @EqualsAndHashCode.Include
    private String idOrder;
    private String idUser;
    private String emailUser;
    private State stateOrder;
    private List<EventVO> cart;
    private BigDecimal transactionAmount;
    private LocalDateTime creationDate;

    public PurchaseOrder(PurchaseOrder purchaseOrder) {
    }
}
