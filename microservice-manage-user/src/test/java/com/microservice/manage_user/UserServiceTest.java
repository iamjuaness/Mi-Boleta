package com.microservice.manage_user;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import com.microservice.manage_user.utils.AppUtil;
import com.microservice.manage_user.utils.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AppUtil appUtil;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("123", "Pepe", "Armenia", "123",
                "pepe@gmail.com", "123", "123");

        String passwordEncode = passwordEncoder.encode(registerClientDTO.password());

        User user = userMapper.dtoToEntity(registerClientDTO, passwordEncode);

        // Configurar el mock para que devuelva el usuario al guardar
        when(userRepository.save(user)).thenReturn(user);

        // Llamar al método del servicio que estás probando
        User userRegister = userService.signUp(registerClientDTO);

        // Verificar que el resultado sea el esperado
        assertEquals(user, userRegister);

        // Verificar que el método save del repositorio fue llamado una vez
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSignUpThrowsIllegalStateExceptionWhenEmailAlreadyInUse() {
        // Preparar datos de prueba
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("123", "Pepe", "Armenia", "123",
                "pepe@gmail.com", "123", "123");

        // Configurar el mock para que `checkEmail` devuelva `true`
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(true);

        // Ejecutar el método y verificar que se lanza la excepción
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            userService.signUp(registerClientDTO);
        });

        assertEquals("Mail pepe@gmail.com is already in use", thrown.getMessage());
    }

    @Test
    void testSignUpThrowsDuplicateKeyExceptionWhenIdUserAlreadyInUse() {
        // Preparar datos de prueba
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("123", "Pepe", "Armenia", "123",
                "pepe@gmail.com", "123", "123");

        // Configurar los mocks
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(false);
        when(appUtil.checkIdUser(registerClientDTO.idUser())).thenReturn(true);

        // Ejecutar el método y verificar que se lanza la excepción
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> {
            userService.signUp(registerClientDTO);
        });

        assertEquals("IdUser 123 is already in use", thrown.getMessage());
    }

    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenDTOIsNull() {
        // Ejecutar el método y verificar que se lanza la excepción
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(null);
        });

        assertEquals("The login DTO and its fields cannot be null or empty.", thrown.getMessage());
    }

    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenEmailIsNullOrEmpty() {
        // DTO con email nulo
        LoginClientDTO dtoWithNullEmail = new LoginClientDTO(null, "password123");

        // DTO con email vacío
        LoginClientDTO dtoWithEmptyEmail = new LoginClientDTO("", "password123");

        // Ejecutar el método y verificar que se lanza la excepción para email nulo
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(dtoWithNullEmail);
        });
        assertEquals("The login DTO and its fields cannot be null or empty.", thrown1.getMessage());

        // Ejecutar el método y verificar que se lanza la excepción para email vacío
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(dtoWithEmptyEmail);
        });
        assertEquals("The login DTO and its fields cannot be null or empty.", thrown2.getMessage());
    }

    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenPasswordIsNullOrEmpty() {
        // DTO con password nulo
        LoginClientDTO dtoWithNullPassword = new LoginClientDTO("test@example.com", null);

        // DTO con password vacío
        LoginClientDTO dtoWithEmptyPassword = new LoginClientDTO("test@example.com", "");

        // Ejecutar el método y verificar que se lanza la excepción para password nulo
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(dtoWithNullPassword);
        });
        assertEquals("The login DTO and its fields cannot be null or empty.", thrown1.getMessage());

        // Ejecutar el método y verificar que se lanza la excepción para password vacío
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(dtoWithEmptyPassword);
        });
        assertEquals("The login DTO and its fields cannot be null or empty.", thrown2.getMessage());
    }

    @Test
    void testLoginReturnsEmptyWhenUserNotFound() {
        // Preparar datos de prueba
        LoginClientDTO loginClientDTO = new LoginClientDTO("nonexistent@example.com", "password123");

        // Configurar el mock para que `findByEmailAddress` devuelva un Optional vacío
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.empty());

        // Ejecutar el método
        Optional<User> result = userService.login(loginClientDTO);

        // Verificar que el resultado es Optional.empty()
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoginReturnsEmptyWhenPasswordDoesNotMatch() {
        // Preparar datos de prueba
        LoginClientDTO loginClientDTO = new LoginClientDTO("test@example.com", "wrongpassword");
        User user = new User();
        user.setEmailAddress("test@example.com");
        user.setPassword("hashedpassword123");

        // Configurar los mocks
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginClientDTO.password(), user.getPassword())).thenReturn(false);

        // Ejecutar el método
        Optional<User> result = userService.login(loginClientDTO);

        // Verificar que el resultado es Optional.empty()
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoginReturnsUserWhenCredentialsAreValid() {
        // Preparar datos de prueba
        LoginClientDTO loginClientDTO = new LoginClientDTO("test@example.com", "password123");
        User user = new User();
        user.setEmailAddress("test@example.com");
        user.setPassword("hashedpassword123");

        // Configurar los mocks
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginClientDTO.password(), user.getPassword())).thenReturn(true);

        // Ejecutar el método
        Optional<User> result = userService.login(loginClientDTO);

        // Verificar que el resultado contiene al usuario
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }



}
