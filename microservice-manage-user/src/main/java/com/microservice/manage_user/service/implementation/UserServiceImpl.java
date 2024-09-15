package com.microservice.manage_user.service.implementation;

import com.microservice.manage_user.persistence.model.enums.State;
import com.microservice.manage_user.presentation.advice.ResourceNotFoundException;
import com.microservice.manage_user.presentation.dto.*;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.service.interfaces.UserService;
import com.microservice.manage_user.utils.AppUtil;
import com.microservice.manage_user.utils.mapper.UserMapper;
import com.mongodb.MongoException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    final UserRepository userRepository;
    final AppUtil appUtil;
    final UserMapper userMapper;
    final PasswordEncoder passwordEncoder;
    final MongoTemplate mongoTemplate;

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
     * @throws IllegalStateException
     */
    @Override
    public State signUp(RegisterClientDTO registerClientDTO) throws IllegalStateException {

        try{
            if(appUtil.checkEmail(registerClientDTO.emailAddress())){
                throw new IllegalStateException("Mail " + registerClientDTO.emailAddress() + " is already in use");
            }

            if(appUtil.checkIdUser(registerClientDTO.idUser())){
                throw new IllegalStateException("IdUser " + registerClientDTO.idUser() + " is already in use");
            }

            String passwordEncode = passwordEncoder.encode(registerClientDTO.password());

            User user = userMapper.dtoRegisterToEntity(registerClientDTO, passwordEncode);

            userRepository.save(user);

            return State.SUCCESS;

        } catch (MongoException e){
            e.printStackTrace();
            return State.ERROR;
        }
    }

    /**
     * -This method allows a user to log in to the platform.
     *
     * @param loginClientDTO DTO with the information required for login
     * @return Optional<User>
     * @throws ErrorResponseException
     */
    @Override
    public ClientDTO login(LoginClientDTO loginClientDTO) throws ErrorResponseException {
        // Verify that the DTO brings valid information
        if (loginClientDTO == null || loginClientDTO.emailAddress() == null ||
                loginClientDTO.password() == null || loginClientDTO.emailAddress().isEmpty() ||
                loginClientDTO.password().isEmpty()){
            throw new IllegalArgumentException("The login DTO and its fields cannot be null or empty.");
        }

        // User search
        Optional<User> optionalUser = userRepository.findByEmailAddress(loginClientDTO.emailAddress());
        // Check that the user exists in the database and that the password matches.
        if (optionalUser.isEmpty() || !passwordEncoder.matches(loginClientDTO.password(), optionalUser.get().getPassword())){
            return new ClientDTO("", "", null, "");
        }

        // If the credentials are valid return the user
        return new ClientDTO(optionalUser.get().getIdUser(), optionalUser.get().getName(),
                optionalUser.get().getRole(), optionalUser.get().getEmailAddress());
    }

    /**
     * -This method allows a user to edit his or her profile information.
     *
     * @param updateUserDTO DTO with the information required for profileEdit
     * @param id User's id
     * @throws ResourceNotFoundException
     * @throws IllegalArgumentException
     */
    @Override
    public void profileEdit(UpdateUserDTO updateUserDTO, String id) throws ResourceNotFoundException, IllegalArgumentException {

        if (updateUserDTO == null || updateUserDTO.name() == null || updateUserDTO.address() == null ||
        updateUserDTO.phoneNumber() == null || updateUserDTO.emailAddress() == null || updateUserDTO.name().isEmpty() ||
        updateUserDTO.address().isEmpty() || updateUserDTO.phoneNumber().isEmpty() || updateUserDTO.emailAddress().isEmpty()){
            throw new IllegalArgumentException("The updateUserDTO and its fields cannot be null or empty.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("The user does not exist."));

        user.setName(updateUserDTO.name());
        user.setAddress(updateUserDTO.address());
        user.setPhoneNumber(updateUserDTO.phoneNumber());
        user.setEmailAddress(updateUserDTO.emailAddress());

        userRepository.save(user);
    }

    @Override
    public User getUser(String id) throws ResourceNotFoundException {

        if (id == null || id.isEmpty()){
            throw new IllegalArgumentException("Id is null");
        }
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("User not exists");
        }
        return optionalUser.get();
    }

    @Override
    public List<User> getUsers() throws ResourceNotFoundException {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()){
            throw new ResourceNotFoundException("Users are empty");
        }
        return users;
    }

    /**
     * -This method allows a user to add tickets to a shopping cart.
     * @param addToCartDTO
     * @throws ErrorResponseException
     */
    @Override
    public void addToCart(AddToCartDTO addToCartDTO) throws ErrorResponseException {//

    }

    /**
     * -This method allows a user to remove tickets from a shopping cart.
     * @throws NotFoundException
     * @throws ErrorResponseException
     */
    @Override
    public void deleteTicketsCart() throws NotFoundException, ErrorResponseException {//

    }

    /**
     * -This method allows a user to clear the shopping cart.
     * @throws ErrorResponseException
     */
    @Override
    public void clearCart() throws ErrorResponseException {//

    }

    /**
     * -This method allows a user to activate his account through a code.
     * @param id
     * @throws ErrorResponseException
     */
    @Override
    public void activateAccount(String id) throws ErrorResponseException { //

    }

    @Override
    public void deleteAccount(String id) {
        try{
            if (id == null || id.isEmpty()){
                throw new IllegalArgumentException("Id is not valid");
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            // Definir la actualización (modificar solo el campo "nombre")
            Update update = new Update();
            update.set("state", State.INACTIVE);

            // Ejecutar la actualización
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (MongoException e){
            e.printStackTrace();
        }
    }
}
