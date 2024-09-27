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
import static org.mockito.Mockito.*;

import com.microservice.auth.service.implementation.MailServiceImpl;
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

	@Mock
	MailServiceImpl mailService;

	@InjectMocks
	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Inicializa los mocks
	}

	@Test
	void loginClient_nullDTO_throwsException() throws Exception {
		//Given
		LoginClientDTO loginClientDTO = null;

		//When and
		TokenDTO loginClient = authService.loginClient(loginClientDTO);
//		Then
		assertNull(loginClient.token());
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
	void loginClient_userNotActive_throwsException() throws Exception {
		//Given
		LoginClientDTO loginClientDTO = new LoginClientDTO("inactive@gmail.com", "12345");
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "inactive@gmail.com", State.INACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		when(manageUserClient.getClient(anyString(), anyString())).thenReturn(responseEntity);

		//When and Then
		TokenDTO loginClient = authService.loginClient(loginClientDTO);

		assertNull(loginClient.token());
	}

	@Test
	void loginClient_tokenNotGenerated_throwsException() throws Exception {
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
		TokenDTO loginClient = authService.loginClient(loginClientDTO);

		// Verificar el mensaje de la excepción
		assertNull(loginClient.token());
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
	void registerClient_Success()  {
		// Given
		RegisterClientDTO registerUserDto = new RegisterClientDTO("1", "John", "Address", "1234567890", "john@example.com", "passworD123", "passworD123");
		MessageDTO<StateDTO> messageDTO = new MessageDTO<>(false, new StateDTO(State.SUCCESS, State.SUCCESS, "1"));
		ResponseEntity<MessageDTO<StateDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);
		String generatedToken = "some-generated-token";

		// When
		when(manageUserClient.registerClient(registerUserDto)).thenReturn(responseEntity);
		when(activationCodeGenerator.generateActivationCode()).thenReturn("123456");
		when(jwtUtilsService.generarToken(anyString(), anyMap())).thenReturn(generatedToken);
		when(validatePassword.validarContrasena(registerUserDto.password())).thenReturn(true);
		when(manageUserClient.saveCodeValidation(anyString(), anyString())).thenReturn(null);
		doNothing().when(mailService).sendMail(anyString(), anyString(), anyString());

		// Then
		StateDTO result = authService.registerClient(registerUserDto);
		assertEquals(State.SUCCESS, result.stateRegister());
	}

	@Test
	void registerClient_NullDTO_ThrowsException()  {
		// Given
		RegisterClientDTO registerUserDto = null;
		StateDTO reponse = new StateDTO(State.ERROR, null,null);

		// When & Then
		StateDTO exception = authService.registerClient(registerUserDto);

		assertEquals(reponse, exception);
	}

	@Test
	void registerClient_InvalidPassword_ThrowsException()  {
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
		StateDTO registerClient = authService.registerClient(registerUserDto);

		assertEquals(State.ERROR, registerClient.stateRegister());
	}

	@Test
	void validateAccount_error_validateCode(){

//		Given
		MessageDTO<State> messageDTO = new MessageDTO<>(true,State.ERROR);
		ResponseEntity<MessageDTO<State>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

//		When
		when(manageUserClient.validateCode(anyString(),anyString())).thenReturn(responseEntity);

//		Then
		State validateCode = authService.activationAccount("2020FK" ,"algo");

		assertEquals(State.ERROR, validateCode);
	}

	@Test
	void validateAccount_error_Null_ThrowsException()  {
//		Given

//		When
		when(manageUserClient.validateCode(anyString(),anyString())).thenReturn(null);

//		Then
		assertEquals(State.ERROR, authService.activationAccount("2020FK" ,"algo"));

	}

	@Test
	void validateAccount_success(){
//		given
		MessageDTO<State> messageDTO = new MessageDTO<>(false, State.SUCCESS);
		ResponseEntity<MessageDTO<State>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

//		when
		when(manageUserClient.validateCode(anyString(),anyString())).thenReturn(responseEntity);
		when(manageUserClient.activateAccount(anyString())).thenReturn(responseEntity);

//		then
		State validateCode = authService.activationAccount("2020FK" ,"algo");
		assertEquals(State.SUCCESS, validateCode);
	}

	@Test
	void forgotPassword_error_null_client(){
//		When
		when(manageUserClient.getUserByEmail(anyString())).thenReturn(null);

//		Then
		State forgotPassword = authService.forgotPassword("algo");
		assertEquals(State.ERROR, forgotPassword);
	}
	@Test
	void forgotPassword_success(){
//		Given
		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "test@example.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTO = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);
		String some = "some";

//		When
		when(manageUserClient.getUserByEmail(anyString())).thenReturn(responseEntity);
		when(activationCodeGenerator.generateActivationCode()).thenReturn(some);
		when(manageUserClient.saveCodeValidation(anyString(), anyString())).thenReturn(null);
		doNothing().when(mailService).sendMail(anyString(), anyString(), anyString());

//		Then
		State forgotPassword = authService.forgotPassword("algo");
		assertEquals(State.SUCCESS, forgotPassword);
	}

	@Test
	void verifyForgotPassword_Null_ThrowsException_client()  {
//		Given

//		When
		when(manageUserClient.getUserByEmail(anyString())).thenReturn(null);
//		Then
		assertEquals(State.ERROR, authService.forgotPassword("algo"));
	}

	@Test
	void verifyForgotPassword_success(){

//		Given
		MessageDTO<State> messageDTO = new MessageDTO<>(false, State.SUCCESS);
		ResponseEntity<MessageDTO<State>> responseEntity = new ResponseEntity<>(messageDTO, HttpStatus.OK);

		ClientDTO clientDTO = new ClientDTO("111111", "John", "client", "test@example.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTOClient = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntityClient = new ResponseEntity<>(messageDTOClient, HttpStatus.OK);

//		When
		when(manageUserClient.getUserByEmail(anyString())).thenReturn(responseEntityClient);
		when(manageUserClient.validateCode(anyString(),anyString())).thenReturn(responseEntity);

//		then
		State forgotPassword = authService.verifyForgotPassword("code", "trej2017@gmail.com");
		assertEquals(State.SUCCESS, forgotPassword);
	}

	@Test
	void changePassword_Success() {
		// Given
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("newPassworD123", "test@example.com");

		ClientDTO clientDTO = new ClientDTO("1", "John", "client", "test@example.com", State.ACTIVE);
		MessageDTO<ClientDTO> messageDTOClient = new MessageDTO<>(false, clientDTO);
		ResponseEntity<MessageDTO<ClientDTO>> responseEntityClient = new ResponseEntity<>(messageDTOClient, HttpStatus.OK);

		MessageDTO<State> messageDTOState = new MessageDTO<>(false, State.SUCCESS);
		ResponseEntity<MessageDTO<State>> responseEntityUpdatePassword = new ResponseEntity<>(messageDTOState, HttpStatus.OK);

		String generatedToken = "generated-auth-token";

		// When
		when(manageUserClient.getUserByEmail(anyString())).thenReturn(responseEntityClient);
		when(validatePassword.validarContrasena(anyString())).thenReturn(true);
		when(manageUserClient.updatePassword(anyString(), anyString())).thenReturn(responseEntityUpdatePassword);
		when(jwtUtilsService.generarToken(anyString(), anyMap())).thenReturn(generatedToken);

		// Then
		TokenDTO result = authService.changePassword(changePasswordDTO);

		// Assertions
		assertNotNull(result);
		assertEquals(generatedToken, result.token());

		// Verificar que se hayan hecho las llamadas correctas
		verify(manageUserClient).getUserByEmail(changePasswordDTO.emailAddress());
		verify(manageUserClient).updatePassword(changePasswordDTO.newPassword(), changePasswordDTO.emailAddress());
		verify(validatePassword).validarContrasena(changePasswordDTO.newPassword());
		verify(jwtUtilsService).generarToken(eq(changePasswordDTO.emailAddress()), anyMap());
	}

	@Test
	void changePassword_InvalidPassword() {
		// Given
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("invalid", "test@example.com");

		// Simular que la contraseña no es válida
		when(validatePassword.validarContrasena(anyString())).thenReturn(false);

		// When
		TokenDTO result = authService.changePassword(changePasswordDTO);

		// Then
		assertNotNull(result);
		assertNull(result.token());  // El token debe ser nulo si no pasa la validación de contraseña

	}

	@Test
	void changePassword_UserNotFound() {
		// Given
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("newPassworD123", "test@example.com");

		// Simular que el usuario no se encontró
		when(manageUserClient.getUserByEmail(anyString())).thenThrow(new NullPointerException("No se ha encontrado el cliente"));

		// When
		TokenDTO result = authService.changePassword(changePasswordDTO);

		// Then
		assertNotNull(result);
		assertNull(result.token());  // El token debe ser nulo si el cliente no se encontró

		// Verificar que la contraseña no se intentó actualizar y el token no se generó
		verify(manageUserClient).getUserByEmail(anyString());
		verify(manageUserClient, never()).updatePassword(anyString(), anyString());
		verify(jwtUtilsService, never()).generarToken(anyString(), anyMap());
	}
}
