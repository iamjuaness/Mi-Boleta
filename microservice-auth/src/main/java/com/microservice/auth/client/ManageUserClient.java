package com.microservice.auth.client;

import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.ClientDTO;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
import com.microservice.auth.presentation.dto.RegisterClientDTO;
import com.microservice.auth.presentation.dto.StateDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-manage-user",url = "localhost:8082/api/request-user")
public interface ManageUserClient {

   @GetMapping("/login-client")
    ResponseEntity<MessageDTO<ClientDTO>> getClient(@RequestParam("emailAddress")String emailAddress, @RequestParam("password")String password);

   @PostMapping("/signup-client")
   ResponseEntity<MessageDTO<StateDTO>> registerClient(@Valid @RequestBody RegisterClientDTO registerClientDTO);

   @PutMapping("/save-code-validation")
    ResponseEntity<MessageDTO<State>> saveCodeValidation(@RequestParam("code") String code , @RequestParam("id") String idUser);

    @PutMapping("/validate-code")
    ResponseEntity<MessageDTO<State>> validateCode(@RequestParam("code") String code , @RequestParam("idUser") String idUser);

    @PutMapping("/activate-account/{id}")
    ResponseEntity<MessageDTO<State>> activateAccount(@PathVariable String id);


}
