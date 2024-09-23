package com.microservice.auth;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.dto.ClientDTO;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
import com.microservice.auth.presentation.dto.LoginClientDTO;
import com.microservice.auth.presentation.dto.TokenDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.microservice.auth.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class AuthServiceImplTests {

	@Mock
	ManageUserClient manageUserClient;

	@Mock
	JwtUtils jwtUtilsService;

	@InjectMocks
	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Inicializa los mocks
	}


	@Test
	public void loginClientTest() throws Exception {
		//Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("trej2017@gmail.com", "1005091Je");
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "trej2017@gmail.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<ClientDTO>(false,clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<MessageDTO<ClientDTO>>(messageDTO, HttpStatus.OK);

		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);
		when(jwtUtilsService.generarToken(anyString(),anyMap())).thenReturn("token");

		//When
		TokenDTO tokenDTO = authService.loginClient(loginClientDTO);

		//Then
		assertNotNull(tokenDTO);
	}

}
