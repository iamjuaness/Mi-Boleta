package com.microservice.manage_user.utils;

import com.microservice.manage_user.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUtil {

    final UserRepository userRepository;

    public AppUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * - Method to verify if the email already exists.
     * @param email
     * @return
     */
    public boolean checkEmail(String email){
        return userRepository.findByEmailAddress(email).isPresent();
    }

    /**
     * - Method to verify if the idUser already exists.
     * @param id
     * @return
     */
    public boolean checkIdUser(String id){
        return userRepository.findById(id).isPresent();
    }
}
