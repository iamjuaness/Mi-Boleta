package com.microservice.manage_user.persistence.repository;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAddress(String emailAddress);
    @Query("{ 'state' : ?0 }")
    List<User> findByState(State state);
}
