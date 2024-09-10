package com.microservice.manage_user.service.implementation;

import com.microservice.manage_user.presentation.dto.AddToCartDTO;
import com.microservice.manage_user.presentation.dto.LoginClientDTO;
import com.microservice.manage_user.presentation.dto.RegisterClientDTO;
import com.microservice.manage_user.presentation.dto.UpdateUserDTO;
import com.microservice.manage_user.persistence.model.entities.User;
import com.microservice.manage_user.persistence.repository.UserRepository;
import com.microservice.manage_user.service.interfaces.UserService;
import com.microservice.manage_user.utils.AppUtil;
import com.microservice.manage_user.utils.mapper.UserMapper;
import jakarta.ws.rs.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    final UserRepository userRepository;
    final AppUtil appUtil;
    final UserMapper userMapper;
    final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AppUtil appUtil, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appUtil = appUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * -This method allows you to create a user on the platform.
     * @param registerClientDTO
     * @return
     * @throws ErrorResponseException
     */
    @Override
    public User signUp(RegisterClientDTO registerClientDTO) throws IllegalStateException {

        if(appUtil.checkEmail(registerClientDTO.emailAddress())){
            throw new IllegalStateException("Mail " + registerClientDTO.emailAddress() + " is already in use");
        }

        if(appUtil.checkIdUser(registerClientDTO.idUser())){
            throw new DuplicateKeyException("IdUser " + registerClientDTO.idUser() + " is already in use");
        }

        String passwordEncode = passwordEncoder.encode(registerClientDTO.password());

        User user = userMapper.dtoToEntity(registerClientDTO, passwordEncode);

        return userRepository.save(user);
    }

    /**
     * -This method allows a user to log in to the platform.
     *
     * @param loginClientDTO
     * @return
     * @throws ErrorResponseException
     */
    @Override
    public Optional<User> login(LoginClientDTO loginClientDTO) throws ErrorResponseException {

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
            return Optional.empty();
        }

        // If the credentials are valid return the user
        return optionalUser;
    }

    /**
     * -This method allows a user to edit his or her profile information.
     * @param updateUserDTO
     * @param id
     * @return
     * @throws NotFoundException
     */
    @Override
    public User profileEdit(UpdateUserDTO updateUserDTO, String id) throws NotFoundException {
        return null;
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
}
