package com.microservice.auth;

import com.microservice.auth.client.ManageUserClient;
import com.microservice.auth.persistence.model.enums.State;
import com.microservice.auth.presentation.advice.ResourceNotFoundException;
import com.microservice.auth.presentation.dto.*;
import com.microservice.auth.presentation.dto.HTTP.MessageDTO;
import com.microservice.auth.service.implementation.AuthServiceImpl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.microservice.auth.utils.ActivationCodeGenerator;
import com.microservice.auth.utils.JwtUtils;
import com.microservice.auth.utils.ValidatePassword;
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

	@Mock
	ValidatePassword validatePassword;

	@Mock
	ActivationCodeGenerator activationCodeGenerator;

	@InjectMocks
	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Inicializa los mocks
	}

	@Test
	void loginClient_nullDTO_throwsException() {
		//Given
		LoginClientDTO loginClientDTO = null;

		//When and Then
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.loginClient(loginClientDTO);
		});

		assertEquals("Por favor ingrese un valor valido", exception.getMessage());
	}

	@Test
	void loginClient_userNotFound_throwsException() {
		//Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("nonexistent@gmail.com", "wrongPassword");
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);

		//When and Then
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			authService.loginClient(loginClientDTO);
		});

		assertEquals("No se ha encontrado un usuario asocioado a la información proporcionada", exception.getMessage());
	}

	@Test
	void loginClient_userNotActive_throwsException()  {
		//Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("inactive@gmail.com", "12345");
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "inactive@gmail.com", State.INACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);

		//When and Then
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.loginClient(loginClientDTO);
		});

		assertEquals("Por favor active su cuenta", exception.getMessage());
	}

	@Test
	void loginClient_tokenNotGenerated_throwsException() {
		// Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("test@example.com", "password123");
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "test@example.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		// Simular la respuesta del servicio externo
		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);
		// Simular que el token no fue generado correctamente (null o vacío)
		when(jwtUtilsService.generarToken(anyString(), anyMap())).thenReturn(null);

		// When & Then
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.loginClient(loginClientDTO);
		});

		// Verificar el mensaje de la excepción
		assertEquals("No se ha generado el token correctamente", exception.getMessage());
	}

	@Test
	void loginClient_TokenGenerated() throws Exception {

		// Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("test@example.com", "password123");
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "test@example.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		// Simular la respuesta del servicio externo
		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);
		// Simular que el token fue generado correctamente
		String generatedToken = "some-generated-token";
		when(jwtUtilsService.generarToken(anyString(), anyMap())).thenReturn(generatedToken);

		// When
		TokenDTO tokenDTO = authService.loginClient(loginClientDTO);

		// Then
		assertNotNull(tokenDTO);
		assertEquals(generatedToken, tokenDTO.token()); // Verificar que el token es el esperado
	}

	@Test
	void registerClient_Success() throws Exception {
		// Given
		RegisterClientDTO registerUserDto = new RegisterClientDTO("1", "John", "Address", "1234567890", "john@example.com", "passworD123", "passworD123");
		MessageDTO<StateDTO> messageDTO = new MessageDTO<>(false, new StateDTO(State.SUCCESS, State.ACTIVE, "1"));
		ResponseEntity<MessageDTO<StateDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);
		String generatedToken = "some-generated-token";

		when(manageUserClient.registerClient(registerUserDto)).thenReturn(responseEntity);
		when(activationCodeGenerator.generateActivationCode()).thenReturn("123456");
		when(jwtUtilsService.generarToken(anyString(), anyMap())).thenReturn(generatedToken);
		when(validatePassword.validarContrasena(registerUserDto.password())).thenReturn(true);

		// When
		StateDTO result = authService.registerClient(registerUserDto);

		// Then

		assertEquals(State.SUCCESS, result.stateRegister());
		assertNotNull(result.idUser());
	}

	@Test
	void registerClient_NullDTO_ThrowsException() throws Exception {
		// Given
		RegisterClientDTO registerUserDto = null;
		StateDTO reponse = new StateDTO(State.ERROR, null,null);

		// When & Then
		StateDTO exception = authService.registerClient(registerUserDto);

		assertEquals(reponse, exception);
	}

	@Test
	void registerClient_InvalidPassword_ThrowsException() throws Exception {
		// Given
		RegisterClientDTO registerUserDto = new RegisterClientDTO("1", "John", "Address", "1234567890", "john@example.com", "pass", "pass");

		// When & Then
		StateDTO reponse = new StateDTO(State.ERROR, null,null);

		// When & Then
		StateDTO exception = authService.registerClient(registerUserDto);

		assertEquals(reponse, exception);
	}

	@Test
	void registerClient_StateRegisterFailed_ThrowsException()  {
		// Given
		RegisterClientDTO registerUserDto = new RegisterClientDTO("1", "John", "Address", "1234567890", "john@example.com", "password123", "password123");
		MessageDTO<StateDTO> messageDTO = new MessageDTO<>(false, new StateDTO(State.ERROR, State.ACTIVE, "1"));
		ResponseEntity<MessageDTO<StateDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		when(manageUserClient.registerClient(registerUserDto)).thenReturn(responseEntity);

		// When & Then
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.registerClient(registerUserDto);
		});
		assertEquals("El estado del registro no es satisfactorio", exception.getMessage());
	}









}
