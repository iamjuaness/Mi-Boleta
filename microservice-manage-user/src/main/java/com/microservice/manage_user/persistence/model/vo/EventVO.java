package com.microservice.manage_user.persistence.model.vo;

import lombok.*;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventVO {

    @EqualsAndHashCode.Include
    @Id
    private String idEvent;
    private String name;
}
