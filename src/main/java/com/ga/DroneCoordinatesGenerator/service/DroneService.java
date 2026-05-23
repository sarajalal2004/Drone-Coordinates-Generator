package com.ga.DroneCoordinatesGenerator.service;

import com.ga.DroneCoordinatesGenerator.exception.AccessDeniedException;
import com.ga.DroneCoordinatesGenerator.exception.BadRequestException;
import com.ga.DroneCoordinatesGenerator.exception.InformationExistException;
import com.ga.DroneCoordinatesGenerator.exception.InformationNotFoundException;
import com.ga.DroneCoordinatesGenerator.model.Drone;
import com.ga.DroneCoordinatesGenerator.model.User;
import com.ga.DroneCoordinatesGenerator.model.requests.RentDroneRequest;
import com.ga.DroneCoordinatesGenerator.repository.DroneRepository;
import com.ga.DroneCoordinatesGenerator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ga.DroneCoordinatesGenerator.service.UserService.getCurrentLoggedInUser;

@Service
public class DroneService {
    private DroneRepository droneRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public DroneService(DroneRepository droneRepository,
                        UserService userService,
                        UserRepository userRepository){
        this.droneRepository = droneRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<Drone> getDrones(){
        return droneRepository.findAll();
    }

    public Drone getDrone(Long droneId){
        return droneRepository.findById(droneId).orElseThrow(
                () -> new InformationNotFoundException("No drone with Id " + droneId + " exists")
        );
    }

    public Drone createDrone(Drone drone){
        if(droneRepository.existsBySerial(drone.getSerial()))
            throw new InformationExistException("Drone with serial " + drone.getSerial() + " is already exists");
        User user = getCurrentLoggedInUser();
        drone.setOwner(user);
        if(user.getRole().equals(User.Role.USER) || user.getRole().equals(User.Role.RENTER)){
            drone.getOwner().setRole(User.Role.OWNER);
        }
        return droneRepository.save(drone);
    }

    public Drone updateDrone(Long droneId, Drone drone){
        Drone droneObj = droneRepository.findById(droneId).orElseThrow(
                () -> new InformationNotFoundException("No drone with Id " + droneId + " exists")
        );
        if(droneRepository.existsBySerial(drone.getSerial()) && droneRepository.findBySerial(drone.getSerial()).get().getId() != droneId)
        droneObj.setSerial(drone.getSerial());
        return droneRepository.save(droneObj);
    }

    public Drone deleteDrone(Long droneId){
        Drone droneObj = droneRepository.findById(droneId).orElseThrow(
                () -> new InformationNotFoundException("No drone with Id " + droneId + " exists")
        );
        droneObj.setStatus(Drone.DroneStatus.INACTIVE);
        return droneRepository.save(droneObj);
    }

    public ResponseEntity rentDrone(RentDroneRequest rentDroneRequest){
        Drone drone = droneRepository.findById(rentDroneRequest.getDroneId()).orElseThrow(
                () -> new InformationNotFoundException("No drone with Id " + rentDroneRequest.getDroneId() + " exists")
        );
        if(!drone.getOwner().equals(getCurrentLoggedInUser()) && !getCurrentLoggedInUser().getRole().equals(User.Role.ADMIN))
            throw new AccessDeniedException("Only drone owner or admin allowed to assign it for rent");
        User rentUser = userRepository.findById(rentDroneRequest.getRenterUserId()).orElseThrow(
                () -> new InformationNotFoundException("No user with Id " + rentDroneRequest.getRenterUserId() + " exists")
        );
        drone.setRenter(rentUser);
        if(rentUser.equals(getCurrentLoggedInUser()))
            throw new BadRequestException("Rented User is already an owner for this drone");
        if(rentUser.getRole().equals(User.Role.USER)){
            drone.getRenter().setRole(User.Role.RENTER);
        }
        droneRepository.save(drone);
        return ResponseEntity.ok("Drone " + drone.getSerial() + "  have been rented to " + rentUser.getUserProfile().getFirstName() + " " + rentUser.getUserProfile().getLastName() + " successfully");
    }
}
