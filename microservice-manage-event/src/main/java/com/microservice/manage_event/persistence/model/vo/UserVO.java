package com.microservice.manage_event.persistence.model.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserVO {

    @EqualsAndHashCode.Include
    private String idUser;
}
