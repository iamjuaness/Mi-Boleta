package com.microservice.manage_event.persistence.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("locations")
public class Location {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;
    private String country;
    private int postal_code;
    private String town;
    private String department;
    private int department_code;
    private String city;
    private int postal_code_alt;
    private double latitude;
    private double longitude;
    private int level;
}
