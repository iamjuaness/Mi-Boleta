package com.microservice.manage_event.client;

import com.microservice.manage_event.presentation.dto.http.MessageDTO;
import com.microservice.manage_user.persistence.model.entities.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "msvc-manage-user",url = "localhost:8082/api/request-user")
public interface ManageUserClient {

    @GetMapping
    ResponseEntity<MessageDTO<List<User>>> getUsers();

    @GetMapping
    ResponseEntity<MessageDTO<User>> getUser(@PathVariable String idUser);
}
