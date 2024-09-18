package com.microservice.auth.client;

import com.microservice.auth.presentation.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-manage-user",url = "localhost:8082/api/manage-user")
public interface ManageUserClient {

   @GetMapping("/login-client")
    ClientDTO getClient(@RequestParam("emailAddress")String emailAddress, @RequestParam("password")String password);

}