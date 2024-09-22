package com.microservice.manage_event.persistence.model.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class LocalityVO {

    @EqualsAndHashCode.Include
    private String idLocality;
    private String nameLocality;
    private String capacityLocality;
    private double priceLocality;
}
