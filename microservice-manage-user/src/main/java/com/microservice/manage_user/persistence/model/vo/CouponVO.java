package com.microservice.manage_user.persistence.model.vo;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CouponVO {

    @EqualsAndHashCode.Include
    @Id
    private String id;
    private String code;
    private Double discount;
    private Date creationDate;
    private Date expirationDate;
    private List<UserVO> userVOS;
}
