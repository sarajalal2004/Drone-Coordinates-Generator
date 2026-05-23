package com.ga.DroneCoordinatesGenerator.controller;

import com.ga.DroneCoordinatesGenerator.model.User;
import com.ga.DroneCoordinatesGenerator.model.UserProfile;
import com.ga.DroneCoordinatesGenerator.model.requests.ChangePasswordRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.EmailRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.LoginRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.UpdateRoleRequest;
import com.ga.DroneCoordinatesGenerator.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "auth/users")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public User createUser(@RequestBody User user) throws MessagingException {
        return userService.createUser(user);
    }

    @GetMapping(path = "/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token){
        return userService.verifyUser(token);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest){
        return userService.loginUser(loginRequest);
    }

    @PostMapping(path = "/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return userService.changePassword(changePasswordRequest);
    }

    @PostMapping(path = "/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody EmailRequest emailRequest) throws MessagingException {
        return userService.forgetPassword(emailRequest);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword){
        return userService.resetPassword(token, newPassword);
    }

    @PostMapping(path = "/inactivate")
    public ResponseEntity<String> deleteUser(@RequestBody EmailRequest emailRequest){
        return userService.deleteUser(emailRequest);
    }

    @PostMapping(path = "/reactivate")
    public ResponseEntity<String> activateUser(@RequestBody EmailRequest emailRequest){
        return userService.activateUser(emailRequest);
    }

    @PostMapping(path = "/update-role")
    public ResponseEntity<String> updateRole(@RequestBody UpdateRoleRequest updateRoleRequest){
        return userService.updateRole(updateRoleRequest);
    }

    @PutMapping(path = "/update-profile")
    public UserProfile updateProfile(@RequestParam("email") String email, @RequestBody UserProfile userProfile){
        return userService.updateProfile(email, userProfile);
    }

    @PostMapping(path = "/update-profile-picture")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        return userService.uploadProfileImage(file);
    }
}
