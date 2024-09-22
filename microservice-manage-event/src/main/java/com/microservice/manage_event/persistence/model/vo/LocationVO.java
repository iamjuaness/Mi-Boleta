package com.microservice.manage_event.persistence.model.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class LocationVO {

    private String country;
    private String department;
    private String city;
    @EqualsAndHashCode.Include
    private int postalCode;
}
