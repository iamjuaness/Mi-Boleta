package com.microservice.manage_user.persistence.model.entities;

import com.microservice.manage_user.persistence.model.enums.Role;
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
    private String address;
    private Role role;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private List<Object> idShopping;
    private List<Object> idCoupon;
}
