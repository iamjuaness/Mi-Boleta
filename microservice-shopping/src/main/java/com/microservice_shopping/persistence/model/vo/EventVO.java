package com.microservice_shopping.persistence.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventVO {
    @NotBlank(message = "El ID del evento no puede estar vacío")
    private String idEvent;

    @NotBlank(message = "El ID de la localidad no puede estar vacío")
    private String idLocality;

    @NotBlank(message = "El ID de la localidad no puede estar vacío")
    private String nameLocality;

    @NotNull(message = "El valor unitario no puede ser nulo")
    @Positive(message = "El valor unitario debe ser un número positivo")
    private BigDecimal unitValue;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser un número positivo")
    private int quantity;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser un número positivo")
    private  BigDecimal subTotal;
}
