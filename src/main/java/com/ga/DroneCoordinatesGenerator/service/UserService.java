package com.ga.DroneCoordinatesGenerator.service;

import com.ga.DroneCoordinatesGenerator.exception.AuthenticationException;
import com.ga.DroneCoordinatesGenerator.exception.InformationNotFoundException;
import com.ga.DroneCoordinatesGenerator.model.User;
import com.ga.DroneCoordinatesGenerator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new AuthenticationException("No user exists with this email")
        );
    }


}
