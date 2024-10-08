package com.microservice.cart.persistence.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventVO {

    @NotNull(message = "idEventVO is required") private String idEventVO;

    @NotBlank(message = "IdEvent is required") private String idEvent;

    @NotBlank(message = "idLocality is required") private String idLocality;

    @NotNull(message = "unitValue is required")
    @Positive(message = "unitValues must be a positive number")
    private BigDecimal unitValue;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive number")
    private int quantity;

    public BigDecimal calculateTotal() {
        return unitValue.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
