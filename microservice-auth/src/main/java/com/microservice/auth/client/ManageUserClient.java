package com.microservice.auth.client;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.ClientDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-manage-user",url = "localhost:8082/api/manage-user")
public interface ManageUserClient {

   @GetMapping("/login-client")
    ClientDTO getClient(@RequestParam("emailAddress")String emailAddress, @RequestParam("password")String password);

   @PostMapping("/signup-client")
    State registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO);

}
