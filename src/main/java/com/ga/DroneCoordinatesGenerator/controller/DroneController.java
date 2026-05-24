package com.ga.DroneCoordinatesGenerator.controller;

import com.ga.DroneCoordinatesGenerator.model.Drone;
import com.ga.DroneCoordinatesGenerator.model.requests.RentDroneRequest;
import com.ga.DroneCoordinatesGenerator.service.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/drones")
public class DroneController {
    private DroneService droneService;

    @Autowired
    public void setDroneService(DroneService droneService){
        this.droneService = droneService;
    }

    @GetMapping
    public List<Drone> getDrones(){
        return droneService.getDrones();
    }

    @GetMapping(path = "/{droneId}")
    public Drone getDrone(@PathVariable("droneId") Long droneId){
        return droneService.getDrone(droneId);
    }

    @PostMapping
    public Drone createDrone(@RequestBody Drone drone){
        return droneService.createDrone(drone);
    }

    @PutMapping(path = "/{droneId}")
    public Drone updateDrone(@PathVariable("droneId") Long droneId,
                             @RequestBody Drone drone){
        return droneService.updateDrone(droneId, drone);
    }

    @DeleteMapping(path = "/{droneId}")
    public Drone deleteDrone(@PathVariable("droneId") Long droneId){
        return droneService.deleteDrone(droneId);
    }

    // problem
    @PutMapping(path = "/rent")
    public ResponseEntity rentDrone(@RequestBody RentDroneRequest rentDroneRequest){
        return droneService.rentDrone(rentDroneRequest);
    }
}
