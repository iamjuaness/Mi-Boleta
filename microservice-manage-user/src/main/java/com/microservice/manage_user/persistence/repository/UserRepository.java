package com.microservice.manage_user.persistence.repository;

import com.microservice.manage_user.persistence.model.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAddress(String emailAddress);
}
