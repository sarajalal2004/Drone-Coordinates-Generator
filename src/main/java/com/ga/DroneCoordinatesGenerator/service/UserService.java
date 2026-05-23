package com.ga.DroneCoordinatesGenerator.service;

import com.ga.DroneCoordinatesGenerator.exception.AuthenticationException;
import com.ga.DroneCoordinatesGenerator.exception.BadRequestException;
import com.ga.DroneCoordinatesGenerator.exception.InformationExistException;
import com.ga.DroneCoordinatesGenerator.exception.InformationNotFoundException;
import com.ga.DroneCoordinatesGenerator.model.EmailVerificationToken;
import com.ga.DroneCoordinatesGenerator.model.PasswordResetToken;
import com.ga.DroneCoordinatesGenerator.model.User;
import com.ga.DroneCoordinatesGenerator.model.UserProfile;
import com.ga.DroneCoordinatesGenerator.model.requests.ChangePasswordRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.EmailRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.LoginRequest;
import com.ga.DroneCoordinatesGenerator.model.requests.UpdateRoleRequest;
import com.ga.DroneCoordinatesGenerator.repository.EmailVerificationTokenRepository;
import com.ga.DroneCoordinatesGenerator.repository.PasswordResetTokenRepository;
import com.ga.DroneCoordinatesGenerator.repository.UserProfileRepository;
import com.ga.DroneCoordinatesGenerator.repository.UserRepository;
import com.ga.DroneCoordinatesGenerator.security.JWTUtils;
import com.ga.DroneCoordinatesGenerator.security.MyUserDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Value("${spring.mail.username}")
    private String fromMail;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private JavaMailSender mailSender;
    private CloudinaryService cloudinaryService;
    private UserProfileRepository userProfileRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       @Lazy CloudinaryService cloudinaryService,
                       UserProfileRepository userProfileRepository,
                       @Lazy JavaMailSender mailSender){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.cloudinaryService = cloudinaryService;
        this.userProfileRepository = userProfileRepository;
        this.mailSender = mailSender;
    }

    public User createUser(User user) throws MessagingException {
        if(!userRepository.existsByEmail(user.getEmail())){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(User.Status.ACTIVE);
            user.setRole(User.Role.USER);
            user.setVerified(false);
            userRepository.save(user);

            EmailVerificationToken token = new EmailVerificationToken();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            emailVerificationTokenRepository.save(token);

            sendVerificationEmail(user, token.getToken());
            return user;
        }else {
            throw new InformationExistException("Username already used, please try other");
        }
    }

    public void sendVerificationEmail(User user, String token) throws MessagingException {
        //TODO: change when deployed
        String URL = "http://localhost:8080/auth/users/verify?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromMail);
        helper.setTo(user.getEmail());
        helper.setSubject("Verify Email Drone System");
        helper.setText("<h1>Hey " + user.getUserProfile().getFirstName() + "</h1>"+
                        "<p>Please click the following Button to verify</p>"+
                        "<a href=\"" + URL +"\" style='display:inline-block;padding:12px 24px;display:inline-block;padding:12px 24px;font-size:16px;color:white;background-color:#28a745;'>Verify</a>"
                , true);
        mailSender.send(message);
    }

    public ResponseEntity<String> verifyUser(String token){
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Invalid verification token or email already verified"));
        if(emailVerificationToken.getExpiryDate().isAfter(LocalDateTime.now())){
            emailVerificationToken.getUser().setVerified(true);
        }else {
            throw new AuthenticationException("Verification token expired");
        }
        userRepository.save(emailVerificationToken.getUser());
        emailVerificationTokenRepository.delete(emailVerificationToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Email verified Successfully🎉");
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new AuthenticationException("No user exists with this email")
        );
    }

    public ResponseEntity<String> loginUser(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());
        try{
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails = (MyUserDetails) authentication.getPrincipal();
            final String JWT = jwtUtils.generateJwtToken(myUserDetails);
            return ResponseEntity.ok(JWT);
        } catch (LockedException e) {
            throw new AuthenticationException("Error: This account has been deactivated. Please contact an admin for support.");
        } catch (DisabledException e) {
            throw new AuthenticationException("Error: Email not verified. Please verify your email before logging in.");
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Error: Username or password is incorrect");
        }
    }

    public static User getCurrentLoggedInUser(){
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserDetails == null)
            throw new AuthenticationException("No user is logged in currently");
        return myUserDetails.getUser();
    }

    public ResponseEntity<String> changePassword(ChangePasswordRequest request) {
        User loginUser = getCurrentLoggedInUser();
        if (!passwordEncoder.matches(request.getOldPassword(), loginUser.getPassword())) {
            return ResponseEntity.ok("Old password is not correct");
        } else if (request.getOldPassword().equals(request.getNewPassword())){
            return ResponseEntity.ok("New password couldn't be as old password");
        } else {
           loginUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
           userRepository.save(loginUser);
           return ResponseEntity.ok("password has been changed");
        }
    }

    public ResponseEntity<String> forgetPassword(EmailRequest request) throws MessagingException {
        if(userRepository.existsByEmail(request.getEmail())){
            User forgetPassUser = userRepository.findByEmail(request.getEmail()).orElseThrow(
                    () -> new AuthenticationException("No user exists with this email")
            );
            if(forgetPassUser.getStatus().equals(User.Status.INACTIVE))
                throw new AuthenticationException("Error: This account has been deactivated. Please contact an admin for support.");
            PasswordResetToken passwordResetToken =
                    passwordResetTokenRepository
                            .findByUser(forgetPassUser)
                            .orElse(new PasswordResetToken());
            passwordResetToken.setUser(forgetPassUser);
            passwordResetToken.setToken(UUID.randomUUID().toString());
            passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            passwordResetTokenRepository.save(passwordResetToken);
            sendForgetPasswordEmail(request.getEmail(), passwordResetToken.getToken());
        }
        return ResponseEntity.ok("If user exist, email send to reset password");
    }

    private void sendForgetPasswordEmail(String email, String token) throws MessagingException {
        String URL = "http://localhost:8080/auth/users/reset-password";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setFrom(fromMail);
        helper.setSubject("Reset Password - Drone System");
        helper.setText("<p>Please enter your email and password</p>"+
                        "<form method='post' action='" + URL + "'>"+
                        "<input type='hidden' name='token' value='" + token + "'/>"+
                        "<label>New Password</label>"+
                        "<input type='text' name='newPassword'/>"+
                        "</form>"
                , true);
        mailSender.send(message);
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationException("Request is invalid"));
        User resetTokenUser = resetToken.getUser();
        if(newPassword != null){
            resetTokenUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(resetTokenUser);
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.ok("Password has been reset Successfully! 😁");
        }else{
            throw new BadRequestException("The password is not accepted");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(EmailRequest request){
        User deleteUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InformationNotFoundException("Invalid email"));
        deleteUser.setStatus(User.Status.INACTIVE);
        userRepository.save(deleteUser);
        return ResponseEntity.ok("User deleted Successfully");

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> activateUser(EmailRequest request){
        User activeUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InformationNotFoundException("Invalid email"));
        activeUser.setStatus(User.Status.ACTIVE);
        userRepository.save(activeUser);
        return ResponseEntity.ok("User activated Successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateRole(UpdateRoleRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InformationNotFoundException("Invalid email"));
        user.setRole(request.getRole());
        userRepository.save(user);
        return ResponseEntity.ok("User role updated Successfully");
    }

    public UserProfile updateProfile(String email, UserProfile userProfileOjb){
        if(!email.equals(getCurrentLoggedInUser().getEmail())
                && getCurrentLoggedInUser().getRole().equals(User.Role.ADMIN))
        {
            throw new AuthenticationException("Firefighter could update only his profile.");
        }
        UserProfile userProfile = userRepository.findByEmail(email).get().getUserProfile();
        userProfile.setFirstName(userProfileOjb.getFirstName());
        userProfile.setLastName(userProfileOjb.getLastName());
        userProfile.setCpr(userProfileOjb.getCpr());
        userProfile.setAddress(userProfileOjb.getAddress());
        userProfile.setBirthDate(userProfileOjb.getBirthDate());
        userProfile.setPhoneNumber(userProfileOjb.getPhoneNumber());

        return userProfileRepository.save(userProfile);
    }

    public ResponseEntity<String> uploadProfileImage(MultipartFile file) throws IOException {
        String filename = cloudinaryService.uploadProfileImage(file, getCurrentLoggedInUser().getEmail());
        UserProfile userProfile = getCurrentLoggedInUser().getUserProfile();
        userProfile.setProfileImageURL(filename);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok("Uploaded: " + filename);
    }
}
