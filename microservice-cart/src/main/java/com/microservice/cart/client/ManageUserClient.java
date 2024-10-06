package com.microservice.cart.client;

import com.microservice.cart.persistence.model.enums.State;
import com.microservice.cart.presentation.dto.AddToCartDTO;
import com.microservice.cart.presentation.dto.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-manage-user", url = "localhost:8082/api/manage-user")
public interface ManageUserClient {

    @PutMapping(value = "/add-cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageDTO<State>> addToCart(@RequestBody AddToCartDTO addToCartDTO, @RequestParam String idUser);
}
