package com.microservice.manage_user.service.implementation;

import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.service.exception.ErrorResponseException;
import com.microservice.manage_user.service.interfaces.UserService;
import com.microservice.manage_user.utils.AppUtil;
import com.microservice.manage_user.utils.mapper.UserMapper;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    final UserRepository userRepository;
    final AppUtil appUtil;
    final UserMapper userMapper;
    final PasswordEncoder passwordEncoder;
    final MongoTemplate mongoTemplate;

    private static final String NOT_FOUND = "User not found";
    private static final String ID_NULL = "Id is not valid";
    private static final String PARAMETER_NULL = "parameter are not valid";
    private static final String FAILED_DELETE_ACCOUNT = "Failed to deactivate account";

    public UserServiceImpl(UserRepository userRepository, AppUtil appUtil, UserMapper userMapper, PasswordEncoder passwordEncoder, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.appUtil = appUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * -This method allows you to create a user on the platform.
     *
     * @param registerClientDTO DTO with the information required for registration
     * @return User
     * @throws IllegalStateException if emailAddress or idUser are already in use
     */
    @Override
    public State signUp(RegisterClientDTO registerClientDTO) throws IllegalStateException {

        // Verify that the email is not repeated
        if (appUtil.checkEmail(registerClientDTO.emailAddress())) {
            throw new IllegalStateException("Email " + registerClientDTO.emailAddress() + " is already in use");
        }

        // Verify that the idUser is not repeated
        if (appUtil.checkIdUser(registerClientDTO.idUser())) {
            throw new IllegalStateException("IdUser " + registerClientDTO.idUser() + " is already in use");
        }

        try {

            // Encoding the password
            String passwordEncode = passwordEncoder.encode(registerClientDTO.password());

            // Convert dto to a user entity
            User user = userMapper.dtoRegisterToEntity(registerClientDTO, passwordEncode);

            // Save in the database
            userRepository.save(user);

            // return success state
            return State.SUCCESS;

        } catch (MongoException e) {
            // return error state
            return State.ERROR;
        }
    }

    /**
     * -This method allows a user to log in to the platform.
     *
     * @param loginClientDTO DTO with the information required for login
     * @return Optional<User>
     * @throws ErrorResponseException Error Response Exception
     */
    @Override
    public ClientDTO login(LoginClientDTO loginClientDTO) throws ErrorResponseException {

        // Validate that loginDTO is not null
        if (loginClientDTO == null || !StringUtils.hasText(loginClientDTO.emailAddress()) || !StringUtils.hasText(loginClientDTO.password())) {
            throw new IllegalArgumentException("The login DTO and its fields cannot be null or empty.");
        }

        // Get the user of the database, if not found run an ErrorResponseException
        User user = userRepository.findByEmailAddress(loginClientDTO.emailAddress())
                .orElseThrow(() -> new ErrorResponseException("Invalid email or password"));

        // Compares if password match with password of the user in the database
        if (!passwordEncoder.matches(loginClientDTO.password(), user.getPassword())) {
            throw new ErrorResponseException("Invalid email or password");
        }

        // Return a ClientDTO with information of the user that is in the database
        return new ClientDTO(user.getIdUser(), user.getName(), user.getRole(), user.getEmailAddress(), user.getState());
    }

    /**
     * -This method allows a user to edit his or her profile information.
     *
     * @param updateUserDTO DTO with the information required for profileEdit
     * @param id User's id
     * @throws ResourceNotFoundException if it not founds a user
     * @throws IllegalArgumentException if updateUserDTO is null
     */
    @Override
    public void profileEdit(UpdateUserDTO updateUserDTO, String id) throws ResourceNotFoundException, IllegalArgumentException {

        // Validates that updateUserDTO is not null
        if (updateUserDTO == null){
            throw new IllegalArgumentException("UpdateUserDTO cannot be null.");
        }

        // Gets the user that is in the database
        User user = getUser(id);

        // A boolean variable is defined as needsUpdate and is initializing as false
        boolean needsUpdate = false;

        // Validates if name of the user changed
        if (!updateUserDTO.name().equals(user.getName())) {
            user.setName(updateUserDTO.name());
            needsUpdate = true;
        }

        // Validates if address of the user changed
        if (!updateUserDTO.address().equals(user.getAddress())) {
            user.setAddress(updateUserDTO.address());
            needsUpdate = true;
        }

        // Validates if phoneNumber of the user changed
        if (!updateUserDTO.phoneNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(updateUserDTO.phoneNumber());
            needsUpdate = true;
        }

        // Validate if emailAddress of the user changed
        if (!updateUserDTO.emailAddress().equals(user.getEmailAddress())) {
            user.setEmailAddress(updateUserDTO.emailAddress());
            needsUpdate = true;
        }

        // Only saves the information if the parameters changed
        if (needsUpdate) {
            userRepository.save(user);
        }
    }

    /**
     * -This method allows to get one user by him id
     * @param id userId
     * @return User
     * @throws ResourceNotFoundException if not founds a user
     */
    @Override
    public User getUser(String id) throws ResourceNotFoundException {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException(ID_NULL);
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
    }

    /**
     * -This method allows to get all users
     * @return A list of all users in the database
     */
    @Override
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? Collections.emptyList() : users;
    }

    /**
     * -This method allows a user to add tickets to a shopping cart.
     *
     * @param addToCartDTO information for to add to cart
     * @param id user's id
     * @throws ErrorResponseException if is cannot to add to cart
     * @throws IllegalArgumentException if addToCartDTO or id are null
     */
    @Override
    public void addToCart(AddToCartDTO addToCartDTO, String id) throws ErrorResponseException, ResourceNotFoundException {//
        if (addToCartDTO == null || id == null){
            throw new IllegalArgumentException("parameter is null");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update = new Update().addToSet("cartUser", addToCartDTO);

        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

        if (result.getMatchedCount() == 0) {
            throw new ResourceNotFoundException(NOT_FOUND);
        }

        if (result.getModifiedCount() == 0){
            throw new ErrorResponseException("Failed to add to cart");
        }
    }


    /**
     * -This method allows a user to remove tickets from a shopping cart.
     * @param userId is the user's id
     * @param itemId is the item's id
     * @throws ErrorResponseException if not is cannot to remove from shopping cart
     * @throws ResourceNotFoundException if a shopping cart is not found
     */
    @Override
    public void deleteTicketsCart(String userId, String itemId) throws ResourceNotFoundException, ErrorResponseException {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(itemId)) {
            throw new IllegalArgumentException("User ID and Item ID cannot be null or empty.");
        }

        // Create a query to find the user by userId
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));

        // Create an update to pull the item from the user's cart
        Update update = new Update().pull("cartUser", new Query(Criteria.where("_id").is(itemId)));

        // Perform the update operation
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

        // Check if the user was found
        if (result.getMatchedCount() == 0) {
            throw new ResourceNotFoundException(NOT_FOUND);
        }

        // Check if the item was removed from the cart
        if (result.getModifiedCount() == 0) {
            throw new ErrorResponseException("Failed to remove item from cart");
        }
    }


    /**
     * -This method allows a user to clear the shopping cart.
     * @throws ErrorResponseException if is cannot clear the shopping cart
     */
    @Override
    public void clearCart(String userId) throws ErrorResponseException, ResourceNotFoundException {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException(ID_NULL);
        }

        // Create a query to find the user by userId
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));

        // Create an update to clear the cart
        Update update = new Update().set("userCart", new ArrayList<>());

        // Perform the update operation
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

        // Check if the user was found
        if (result.getMatchedCount() == 0) {
            throw new ResourceNotFoundException(NOT_FOUND);
        }

        // Check if the cart was cleared
        if (result.getModifiedCount() == 0) {
            throw new ErrorResponseException("Failed to clear the cart");
        }
    }


    /**
     * -This method allows a user to activate his account through a code.
     *
     * @param id is the idUser
     * @return state
     * @throws ErrorResponseException if is cannot to activate the user's account
     */
    @Override
    public State activateAccount(String id) throws ErrorResponseException {
        try {
            if (!StringUtils.hasText(id)) {
                throw new IllegalArgumentException(ID_NULL);
            }

            // Create a query to find the user by ID
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            // Create an update to set the state to ACTIVE
            Update update = new Update().set("state", State.ACTIVE);

            // Perform the update operation
            UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

            // Check if the user was found
            if (result.getMatchedCount() == 0) {
                throw new ResourceNotFoundException(NOT_FOUND);
            }

            // Check if the state was updated
            if (result.getModifiedCount() == 0) {
                throw new ErrorResponseException("Failed to activate account");
            }
            return State.SUCCESS;
        } catch (ResourceNotFoundException | ErrorResponseException e){
            return State.ERROR;
        }
    }

    /**
     * -This method allows to delete a user's account
     * @param id is the idUser
     * @throws ResourceNotFoundException if user is not found
     * @throws IllegalArgumentException if id is null
     * @throws ErrorResponseException if is cannot to delete account
     */
    @Override
    public void deleteAccount(String id) throws ResourceNotFoundException, IllegalArgumentException, ErrorResponseException {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException(ID_NULL);
        }

        // Create a query to find the user by ID
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        // Create an update to set the state to INACTIVE
        Update update = new Update().set("state", State.INACTIVE);

        // Perform the update operation
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

        // Check if the user was found
        if (result.getMatchedCount() == 0) {
            throw new ResourceNotFoundException(NOT_FOUND);
        }

        // Check if the state was updated
        if (result.getModifiedCount() == 0) {
            throw new ErrorResponseException(FAILED_DELETE_ACCOUNT);
        }
    }

    @Override
    public State updateCode(String code, String id) {
        try {
            // Validate parameter are valid
            if (!StringUtils.hasText(id) || !StringUtils.hasText(code)){
                throw new IllegalArgumentException(PARAMETER_NULL);
            }

            //Create a query to find the user by id
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            //Create an update to set the code
            Update update = new Update().set("code", code);

            //Perform the update operation
            UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

            // Check if the user was found
            if (result.getMatchedCount() == 0) {
                throw new ResourceNotFoundException(NOT_FOUND);
            }

            // Check if the state was updated
            if (result.getModifiedCount() == 0) {
                throw new ErrorResponseException("Failed changing code");
            }

            return State.SUCCESS;

        } catch (ResourceNotFoundException | ErrorResponseException e) {
            return State.ERROR;
        }
    }

    @Override
    public State validateCode(String code, String idUser) {
        try {
            if (!StringUtils.hasText(code) || !StringUtils.hasText(idUser)){
                throw new IllegalArgumentException(PARAMETER_NULL);
            }

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(idUser));

            User result = mongoTemplate.findOne(query, User.class);

            if (result == null){
                throw new NullPointerException(NOT_FOUND);
            }

            if (!code.equals(result.getCode())){
                throw new IllegalArgumentException("Code is not valid");
            }

            deleteCode(code, idUser);

            return State.SUCCESS;
        } catch (IllegalArgumentException | NullPointerException e){
            return State.ERROR;
        }
    }

    @Override
    public State deleteCode(String code, String id){
        try {
            if (!StringUtils.hasText(code) || !StringUtils.hasText(id)){
                throw new IllegalArgumentException(PARAMETER_NULL);
            }

            //Create a query to find the user by id
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            //Create an update to set the code
            Update update = new Update().set("code", null);

            //Perform the update operation
            UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

            // Check if the user was found
            if (result.getMatchedCount() == 0) {
                throw new ResourceNotFoundException(NOT_FOUND);
            }

            // Check if the state was updated
            if (result.getModifiedCount() == 0) {
                throw new ErrorResponseException("Failed delete code");
            }

            return State.SUCCESS;
        } catch (IllegalArgumentException | ResourceNotFoundException e){
            return State.ERROR;
        }
    }
}
