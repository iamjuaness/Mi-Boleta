package com.microservice.manage_user.persistence.model.entities;

import com.microservice.manage_user.persistence.model.enums.Role;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.persistence.model.vo.CouponVO;
import com.microservice.manage_user.persistence.model.vo.EventVO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("users")
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String idUser;
    private String name;
    private State state;
    private String address;
    private Role role;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private List<EventVO> eventVOS;
    private List<CouponVO> couponVOS;

    public User(String idUser, String name, String address, Role role, String phoneNumber, String emailAddress, String password) {
        this.idUser = idUser;
        this.name = name;
        this.address = address;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
    }
}
