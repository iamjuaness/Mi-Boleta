package com.microservice.manage_user;

import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.model.enums.Role;
import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import com.microservice.manage_user.service.exception.ErrorResponseException;
import com.microservice.manage_user.service.implementation.UserServiceImpl;
import com.microservice.manage_user.utils.AppUtil;
import com.microservice.manage_user.utils.mapper.UserMapper;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AppUtil appUtil;

    @InjectMocks
    UserServiceImpl userService;


    private static final String NOT_FOUND = "User not found";
    private static final String ID_NULL = "Id is not valid";

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // SignUp Test

    @Test
    void testSignUp() {
        // Prepare test data
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("123", "Pepe", "Armenia",
                "123", "pepe@gmail.com", "123", "123");

        // Configure the mock to check that the email and ID are not in use
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(false);
        when(appUtil.checkIdUser(registerClientDTO.idUser())).thenReturn(false);

        // Configure the mock for password encoding
        when(passwordEncoder.encode(registerClientDTO.password())).thenReturn("encodedPassword");

        // Configure the mock for DTO to entity conversion
        User user = new User(); // Ensure proper initialization of the user according to mapping
        when(userMapper.dtoRegisterToEntity(registerClientDTO, "encodedPassword")).thenReturn(user);

        // Configure the mock to save the user
        when(userRepository.save(user)).thenReturn(user);

        // Call the method of the service you are testing
        State userRegister = userService.signUp(registerClientDTO);

        // Verify that the result is as expected
        assertEquals(State.SUCCESS, userRegister);

        // Verify that the repository's save method was called once
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSignUpThrowsIllegalStateExceptionWhenEmailAlreadyInUse() {
        // Prepare test data
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("123", "Pepe", "Armenia",
                "123", "pepe@gmail.com", "123", "123");

        // Configure the mock to return true for `checkEmail`
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(true);

        // Execute the method and verify that the exception is thrown
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> userService.signUp(registerClientDTO));

        assertEquals("Email pepe@gmail.com is already in use", thrown.getMessage());
    }

    @Test
    void testSignUpThrowsIllegalStateExceptionWhenIdUserAlreadyInUse() {
        // Prepare test data
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("234", "Pepe", "Armenia",
                "123", "pepe@gmail.com", "123", "123");

        // Configure the mocks
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(false);
        when(appUtil.checkIdUser(registerClientDTO.idUser())).thenReturn(true);

        // Execute the method and verify that the exception is thrown
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> userService.signUp(registerClientDTO));

        assertEquals("IdUser 234 is already in use", thrown.getMessage());
    }

    @Test
    void testSignUpHandlesMongoException() {
        // Prepare test data
        RegisterClientDTO registerClientDTO = new RegisterClientDTO("234", "Pepe", "Armenia", "123",
                "pepe@gmail.com", "123", "123");

        // Configure the mocks
        when(appUtil.checkEmail(registerClientDTO.emailAddress())).thenReturn(false);
        when(appUtil.checkIdUser(registerClientDTO.idUser())).thenReturn(false);
        when(passwordEncoder.encode(registerClientDTO.password())).thenReturn("encodedPassword");
        when(userMapper.dtoRegisterToEntity(registerClientDTO, "encodedPassword")).thenReturn(new User());
        doThrow(new MongoException("Mongo error")).when(userRepository).save(any(User.class));

        // Execute the method
        State result = userService.signUp(registerClientDTO);

        // Verify results
        assertEquals(State.ERROR, result);
    }

    // Login Test

    @Test
    void testLoginSuccess() {
        // Prepare test data
        LoginClientDTO loginClientDTO = new LoginClientDTO("pepe@gmail.com", "password123");

        // Prepare a user with matching credentials
        User user = new User();
        user.setIdUser("1");
        user.setName("Pepe");
        user.setRole(Role.CLIENT);
        user.setEmailAddress("pepe@gmail.com");
        user.setPassword(passwordEncoder.encode("password123"));

        // Configure the mocks
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginClientDTO.password(), user.getPassword())).thenReturn(true);

        // Call the method and verify the result
        ClientDTO result = userService.login(loginClientDTO);

        // Verify that the result matches the expected ClientDTO
        assertEquals("1", result.idUser());
        assertEquals("Pepe", result.name());
        assertEquals(Role.CLIENT, result.role());
        assertEquals("pepe@gmail.com", result.emailAddress());
    }


    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenDTOIsNull() {
        // Execute the method and verify that an exception is thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.login(null));

        assertEquals("The login DTO and its fields cannot be null or empty.", thrown.getMessage());
    }

    @Test
    void testLoginThrowsErrorResponseExceptionWhenEmailIsInvalid() {
        // Prepare test data
        LoginClientDTO loginClientDTO = new LoginClientDTO("invalid@gmail.com", "password123");

        // Configure the mock to return an empty Optional for the email
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.empty());

        // Execute the method and verify that an exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.login(loginClientDTO));

        assertEquals("Invalid email or password", thrown.getMessage());
    }

    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenEmailIsEmpty() {
        // Prepare test data with empty email
        LoginClientDTO loginClientDTO = new LoginClientDTO("", "password123");

        // Execute the method and verify that an exception is thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.login(loginClientDTO));

        assertEquals("The login DTO and its fields cannot be null or empty.", thrown.getMessage());
    }

    @Test
    void testLoginThrowsErrorResponseExceptionWhenPasswordIsInvalid() {
        // Prepare test data
        LoginClientDTO loginClientDTO = new LoginClientDTO("pepe@gmail.com", "wrongPassword");

        // Prepare a user with the correct email but incorrect password
        User user = new User();
        user.setIdUser("1");
        user.setName("Pepe");
        user.setRole(Role.CLIENT);
        user.setEmailAddress("pepe@gmail.com");
        user.setPassword(passwordEncoder.encode("password123"));

        // Configure the mock to return the user for the email
        when(userRepository.findByEmailAddress(loginClientDTO.emailAddress())).thenReturn(Optional.of(user));

        // Configure the mock for passwordEncoder to return false for the incorrect password
        when(passwordEncoder.matches(loginClientDTO.password(), user.getPassword())).thenReturn(false);

        // Execute the method and verify that an exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.login(loginClientDTO));

        assertEquals("Invalid email or password", thrown.getMessage());
    }

    @Test
    void testLoginThrowsIllegalArgumentExceptionWhenPasswordIsEmpty() {
        // Prepare test data with empty password
        LoginClientDTO loginClientDTO = new LoginClientDTO("pepe@gmail.com", "");

        // Execute the method and verify that an exception is thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.login(loginClientDTO));

        assertEquals("The login DTO and its fields cannot be null or empty.", thrown.getMessage());
    }


    // ProfileEdit Test

    @Test
    void testProfileEditSuccess() throws ResourceNotFoundException, IllegalArgumentException {
        // Prepare test data
        String userId = "123";
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("NewName", "NewAddress", "NewPhone", "newemail@example.com");

        // Prepare the existing user
        User existingUser = new User();
        existingUser.setIdUser(userId);
        existingUser.setName("OldName");
        existingUser.setAddress("OldAddress");
        existingUser.setPhoneNumber("OldPhone");
        existingUser.setEmailAddress("oldemail@example.com");

        // Configure mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Call the method
        userService.profileEdit(updateUserDTO, userId);

        // Verify that the user is updated
        assertEquals("NewName", existingUser.getName());
        assertEquals("NewAddress", existingUser.getAddress());
        assertEquals("NewPhone", existingUser.getPhoneNumber());
        assertEquals("newemail@example.com", existingUser.getEmailAddress());

        // Verify that save was called once
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testProfileEditThrowsIllegalArgumentExceptionWhenDTOIsNull() {
        // Prepare test data
        String userId = "123";

        // Execute the method and verify that an exception is thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.profileEdit(null, userId));

        assertEquals("UpdateUserDTO cannot be null.", thrown.getMessage());
    }

    @Test
    void testProfileEditThrowsResourceNotFoundExceptionWhenUserNotFound() {
        // Prepare test data
        String userId = "123";
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("NewName", "NewAddress", "NewPhone", "newemail@example.com");

        // Configure mock to return an empty Optional for user
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute the method and verify that an exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.profileEdit(updateUserDTO, userId));

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testProfileEditNoChanges() throws ResourceNotFoundException, IllegalArgumentException {
        // Prepare test data
        String userId = "123";
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("OldName", "OldAddress", "OldPhone", "oldemail@example.com");

        // Prepare the existing user
        User existingUser = new User();
        existingUser.setIdUser(userId);
        existingUser.setName("OldName");
        existingUser.setAddress("OldAddress");
        existingUser.setPhoneNumber("OldPhone");
        existingUser.setEmailAddress("oldemail@example.com");

        // Configure mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Call the method
        userService.profileEdit(updateUserDTO, userId);

        // Verify that save was not called
        verify(userRepository, never()).save(existingUser);
    }

    // GetUser Test

    @Test
    void testGetUserSuccess() throws ResourceNotFoundException {
        // Prepare test data
        String userId = "123";
        User user = new User();
        user.setIdUser(userId);
        user.setName("John Doe");

        // Configure mock to return the user for the given ID
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Call the method
        User result = userService.getUser(userId);

        // Verify that the result matches the expected user
        assertEquals(userId, result.getIdUser());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testGetUserThrowsIllegalArgumentExceptionWhenIdIsNullOrEmpty() {
        // Prepare test data
        String emptyId = "";

        // Verify that an exception is thrown for null ID
        IllegalArgumentException thrownNull = assertThrows(IllegalArgumentException.class, () -> userService.getUser(null));
        assertEquals(ID_NULL, thrownNull.getMessage());

        // Verify that an exception is thrown for empty ID
        IllegalArgumentException thrownEmpty = assertThrows(IllegalArgumentException.class, () -> userService.getUser(emptyId));
        assertEquals(ID_NULL, thrownEmpty.getMessage());
    }

    // GetUsers Test

    @Test
    void testGetUsersReturnsUsers() {
        // Prepare test data
        User user1 = new User();
        user1.setIdUser("123");
        user1.setName("John Doe");

        User user2 = new User();
        user2.setIdUser("456");
        user2.setName("Jane Doe");

        List<User> userList = Arrays.asList(user1, user2);

        // Configure mock to return a list of users
        when(userRepository.findAll()).thenReturn(userList);

        // Call the method
        List<User> result = userService.getUsers();

        // Verify that the result matches the expected list of users
        assertEquals(userList, result);
    }

    @Test
    void testGetUsersReturnsEmptyListWhenNoUsers() {
        // Configure mock to return an empty list
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the method
        List<User> result = userService.getUsers();

        // Verify that the result is an empty list
        assertTrue(result.isEmpty());
    }

    // AddToCart Test

    @Test
    void testAddToCartSuccess() throws ErrorResponseException, ResourceNotFoundException {
        // Prepare test data
        AddToCartDTO addToCartDTO = new AddToCartDTO("", "", "", BigDecimal.ZERO, 0);
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        // Configure mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Call the method
        userService.addToCart(addToCartDTO, userId);

        // Verify that updateFirst was called with correct parameters
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    @Test
    void testAddToCartThrowsResourceNotFoundExceptionWhenUserNotFound() throws ErrorResponseException {
        // Prepare test data
        AddToCartDTO addToCartDTO = new AddToCartDTO("", "", "", BigDecimal.ZERO, 0);
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Configure mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.addToCart(addToCartDTO, userId));

        assertEquals(NOT_FOUND, thrown.getMessage());
    }

    @Test
    void testAddToCartThrowsErrorResponseExceptionWhenAddFails() {
        // Prepare test data
        AddToCartDTO addToCartDTO = new AddToCartDTO("", "", "", BigDecimal.ZERO, 0);
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        // Configure mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.addToCart(addToCartDTO, userId));

        assertEquals("Failed to add to cart", thrown.getMessage());
    }

    @Test
    void testAddToCartThrowsIllegalArgumentExceptionWhenParametersAreNull() {
        // Prepare test data
        String userId = "123";

        // Verify that an exception is thrown for null addToCartDTO
        IllegalArgumentException thrownNullDTO = assertThrows(IllegalArgumentException.class, () -> userService.addToCart(null, userId));
        assertEquals("parameter is null", thrownNullDTO.getMessage());

        // Verify that an exception is thrown for null id
        AddToCartDTO validDTO = new AddToCartDTO("", "", "", BigDecimal.ZERO, 0);
        IllegalArgumentException thrownNullId = assertThrows(IllegalArgumentException.class, () -> userService.addToCart(validDTO, null));
        assertEquals("parameter is null", thrownNullId.getMessage());
    }

    // Delete Tickets Test

    @Test
    void testDeleteTicketsCartSuccess() throws ResourceNotFoundException, ErrorResponseException {
        // Prepare test data
        String userId = "123";
        String itemId = "456";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Call the method
        userService.deleteTicketsCart(userId, itemId);

        // Verify that updateFirst was called with the correct parameters
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    @Test
    void testDeleteTicketsCartThrowsResourceNotFoundExceptionWhenUserNotFound() throws ErrorResponseException {
        // Prepare test data
        String userId = "123";
        String itemId = "456";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.deleteTicketsCart(userId, itemId));

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testDeleteTicketsCartThrowsErrorResponseExceptionWhenRemovalFails() {
        // Prepare test data
        String userId = "123";
        String itemId = "456";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.deleteTicketsCart(userId, itemId));

        assertEquals("Failed to remove item from cart", thrown.getMessage());
    }

    @Test
    void testDeleteTicketsCartThrowsIllegalArgumentExceptionWhenParametersAreNull() {
        // Prepare test data
        String userId = "123";
        String itemId = null;

        // Verify that an exception is thrown for null itemId
        String finalUserId = userId;
        IllegalArgumentException thrownNullItemId = assertThrows(IllegalArgumentException.class, () -> userService.deleteTicketsCart(finalUserId, null));
        assertEquals("User ID and Item ID cannot be null or empty.", thrownNullItemId.getMessage());

        // Verify that an exception is thrown for null userId
        itemId = "456";
        String finalItemId1 = itemId;
        IllegalArgumentException thrownNullUserId = assertThrows(IllegalArgumentException.class, () -> userService.deleteTicketsCart(null, finalItemId1));
        assertEquals("User ID and Item ID cannot be null or empty.", thrownNullUserId.getMessage());
    }

    // Clear cart Test

    @Test
    void testClearCartSuccess() throws ResourceNotFoundException, ErrorResponseException {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Call the method
        userService.clearCart(userId);

        // Verify that updateFirst was called with the correct parameters
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    @Test
    void testClearCartThrowsResourceNotFoundExceptionWhenUserNotFound() throws ErrorResponseException {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.clearCart(userId));

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testClearCartThrowsErrorResponseExceptionWhenClearFails() {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.clearCart(userId));

        assertEquals("Failed to clear the cart", thrown.getMessage());
    }

    @Test
    void testClearCartThrowsIllegalArgumentExceptionWhenUserIdIsNull() {
        // Verify that an exception is thrown for null userId
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.clearCart(null));
        assertEquals(ID_NULL, thrown.getMessage());
    }

    // Activate Account Test

    // 1. Successful activation case
    @Test
    void testActivateAccount_Success() throws ErrorResponseException {
        // Simulate a successful UpdateResult
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L); // Simulate that the user was found
        when(updateResult.getModifiedCount()).thenReturn(1L); // Simulate that the state was successfully modified

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method
        State result = userService.activateAccount("123456");

        // Verify the result
        assertEquals(State.SUCCESS, result);
        // Verify that the updateFirst method of mongoTemplate was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 2. Case where ID is null or empty
    @Test
    void testActivateAccount_IdNullOrEmpty() {
        // Verify that with an empty ID, IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> userService.activateAccount(""));

        // Verify that with a null ID, IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> userService.activateAccount(null));
    }

    // 3. Case where the user is not found
    @Test
    void testActivateAccount_UserNotFound() throws ErrorResponseException {
        // Simulate an UpdateResult where no user was found (matchedCount is 0)
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify it returns State.ERROR
        State result = userService.activateAccount("123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 4. Case where the account activation fails (user found but state not updated)
    @Test
    void testActivateAccount_FailedToActivate() throws ErrorResponseException {
        // Simulate an UpdateResult where the user was found but the state was not modified
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L); // User was found
        when(updateResult.getModifiedCount()).thenReturn(0L); // But state was not updated

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify it returns State.ERROR
        State result = userService.activateAccount("123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 5. Case of a custom exception (ErrorResponseException)
    @Test
    void testActivateAccount_ExceptionHandling() throws ErrorResponseException {
        // Simulate an exception when trying to perform the update in MongoDB
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenThrow(new ErrorResponseException("Database error"));

        // Execute the method and verify it returns State.ERROR
        State result = userService.activateAccount("123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // Delete Account Test

    @Test
    void testDeleteAccountSuccess() throws ErrorResponseException {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Call the method
        userService.deleteAccount(userId);

        // Verify that updateFirst was called with the correct parameters
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    @Test
    void testDeleteAccountThrowsResourceNotFoundExceptionWhenUserNotFound() throws ErrorResponseException {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> userService.deleteAccount(userId));

        assertEquals(NOT_FOUND, thrown.getMessage());
    }

    @Test
    void testDeleteAccountThrowsErrorResponseExceptionWhenDeactivationFails() {
        // Prepare test data
        String userId = "123";

        // Create a mock UpdateResult
        UpdateResult updateResult = Mockito.mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        // Configure the mock to return the mock UpdateResult
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify that the exception is thrown
        ErrorResponseException thrown = assertThrows(ErrorResponseException.class, () -> userService.deleteAccount(userId));

        assertEquals("Failed to deactivate account", thrown.getMessage());
    }

    @Test
    void testDeleteAccountThrowsIllegalArgumentExceptionWhenUserIdIsNull() {
        // Verify that an exception is thrown for null userId
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.deleteAccount(null));
        assertEquals(ID_NULL, thrown.getMessage());
    }

    @Test
    void testDeleteAccountThrowsIllegalArgumentExceptionWhenUserIdIsEmpty() {
        // Prepare test data
        String userId = "";

        // Verify that an exception is thrown for empty userId
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userService.deleteAccount(userId));
        assertEquals(ID_NULL, thrown.getMessage());
    }

    // Update code tests

    // 1. Successful code update case
    @Test
    void testUpdateCode_Success() throws ErrorResponseException {
        // Simulate a successful UpdateResult
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L); // Simulate user was found
        when(updateResult.getModifiedCount()).thenReturn(1L); // Simulate code was successfully updated

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method
        State result = userService.updateCode("ABC123", "123456");

        // Verify the result is SUCCESS
        assertEquals(State.SUCCESS, result);
        // Verify that the updateFirst method of mongoTemplate was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 2. Case where ID or code is null or empty
    @Test
    void testUpdateCode_IdOrCodeNullOrEmpty() {
        // Verify IllegalArgumentException is thrown if ID or code is empty or null
        assertThrows(IllegalArgumentException.class, () -> userService.updateCode("ABC123", ""));
        assertThrows(IllegalArgumentException.class, () -> userService.updateCode("", "123456"));
        assertThrows(IllegalArgumentException.class, () -> userService.updateCode("", null));
    }

    // 3. Case where the user is not found
    @Test
    void testUpdateCode_UserNotFound() throws ErrorResponseException {
        // Simulate UpdateResult where no user was found (matchedCount is 0)
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(0L);

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify it returns State.ERROR
        State result = userService.updateCode("ABC123", "123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 4. Case where the code update fails (user found but code not modified)
    @Test
    void testUpdateCode_FailedToUpdateCode() throws ErrorResponseException {
        // Simulate UpdateResult where user was found but code was not modified
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getMatchedCount()).thenReturn(1L); // User found
        when(updateResult.getModifiedCount()).thenReturn(0L); // But code was not updated

        // Mock the MongoTemplate to return the expected result
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(updateResult);

        // Execute the method and verify it returns State.ERROR
        State result = userService.updateCode("ABC123", "123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // 5. Case of a custom exception (ErrorResponseException)
    @Test
    void testUpdateCode_ExceptionHandling() throws ErrorResponseException {
        // Simulate an exception when trying to perform the update in MongoDB
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenThrow(new ErrorResponseException("Database error"));

        // Execute the method and verify it returns State.ERROR
        State result = userService.updateCode("ABC123", "123456");

        assertEquals(State.ERROR, result);
        // Verify that the updateFirst method was called exactly once
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

    // Validate code tests

    @Test
    void testValidateCode_UserNotFound() {
        // Simulate no user found by MongoTemplate
        when(mongoTemplate.findOne(any(Query.class), eq(User.class))).thenReturn(null);

        // Should return ERROR when user is not found (NullPointerException caught)
        State result = userService.validateCode("ABC123", "123456");
        assertEquals(State.ERROR, result);
    }

    @Test
    void testValidateCode_InvalidCode() {
        // Simulate a user with a different code
        User mockUser = new User();
        mockUser.setCode("DIFFERENT_CODE");

        // Return the mock user
        when(mongoTemplate.findOne(any(Query.class), eq(User.class))).thenReturn(mockUser);

        // Should return ERROR when code does not match
        State result = userService.validateCode("ABC123", "123456");
        assertEquals(State.ERROR, result);
    }

    // Tests for deleteCode

    @Test
    void testDeleteCode_UserNotFound() {
        // Simulate no user found by MongoTemplate
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(UpdateResult.acknowledged(0, 0L, null));

        // Should return ERROR when user is not found (ResourceNotFoundException caught)
        State result = userService.deleteCode("ABC123", "123456");
        assertEquals(State.ERROR, result);
    }

    @Test
    void testDeleteCode_Success() {
        // Simular que se actualiza correctamente
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        State result = userService.deleteCode("ABC123", "123456");
        assertEquals(State.SUCCESS, result);
    }

}
